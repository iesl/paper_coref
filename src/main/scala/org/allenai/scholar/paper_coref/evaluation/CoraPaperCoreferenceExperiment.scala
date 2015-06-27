package org.allenai.scholar.paper_coref.evaluation

import java.io.{PrintWriter, File}

import cc.factorie.util.DefaultCmdOptions
import org.allenai.scholar.paper_coref.coreference.PaperCoref
import org.allenai.scholar.paper_coref.load.LoadCora


/**
 * Command line options for the Cora Experiment 
 */
class CoraPaperCoreferenceExperimentOpts extends DefaultCmdOptions {
  val input = new CmdOption[List[String]]("input", "Either a directory of files, a filename of files, or a list of files", true)
  val inputEncoding = new CmdOption[String]("input-encoding", "UTF-8", "CODEC", "The encoding of the input files")
  val output = new CmdOption[String]("output", "A directory in which to write the output to (optional)", false)
  val corefAlgorithms = new CmdOption[List[String]]("coref-algorithms", "The names of the coref algorithms to use",true)
}

/**
 * Runs the coreference experiment on the Cora dataset. Writes HTML report of mention clustering.
 */
object CoraPaperCoreferenceExperiment {

  def main(args: Array[String]): Unit = {
    val opts = new CoraPaperCoreferenceExperimentOpts
    opts.parse(args)
    val mentions = opts.input.value.flatMap(file => LoadCora.loadMentionsFromFile(new File(file), opts.inputEncoding.value))
    val corefs = opts.corefAlgorithms.value.map(PaperCoref.apply)
    val experiment = new PaperCoreferenceExperiment(mentions,corefs)
    val result = experiment.run()
    if (opts.output.wasInvoked) {
      val outputDir = new File(opts.output.value)
      outputDir.mkdirs()
      val writer1 = new PrintWriter(new File(outputDir, "results.txt"), "UTF-8")
      writer1.println(result.map( (x) => x._1 + ":\n" + x._2 + "\n\n").mkString("--------------------------------------------\n\n"))
      writer1.close()
      
      corefs.foreach {
        coref =>
          val predictedHtml = HTMLReport.generateHTML(experiment.predictedClustering(coref.name).get, Some(s"Predicted - ${coref.name}"))
          val writer = new PrintWriter(new File(outputDir, coref.name + ".html"), "UTF-8")
          writer.println(predictedHtml)
          writer.close()
      }
      
      val goldHTML = HTMLReport.generateHTML(experiment.goldClustering, Some("Gold Clustering"))
      val writer2 = new PrintWriter(new File(outputDir, "gold.html"), "UTF-8")
      writer2.println(goldHTML)
      writer2.close()
    }

  }
}
