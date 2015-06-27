package org.allenai.scholar.paper_coref.evaluation

import java.io.{PrintWriter, FileReader, BufferedReader, File}

import cc.factorie._
import cc.factorie.util.DefaultCmdOptions
import org.allenai.scholar.paper_coref.coreference.PaperCoref
import org.allenai.scholar.paper_coref.load.{Loader, FormatType}

/**
 * Command line options for the paper coreference experiment on ACL data 
 */
class ACLPaperCoreferenceExperimentOpts extends DefaultCmdOptions {
  val formatType = new CmdOption[String]("format-type", "The format of the input, RPP, ParsCit, Grobid.",true)
  val input = new CmdOption[List[String]]("input", "Either a directory of files, a filename of files, or a list of files", true)
  val inputType = new CmdOption[String]("input-type", "Directory, file of filenames, file", true)
  val inputEncoding = new CmdOption[String]("input-encoding", "UTF-8", "CODEC", "The encoding of the input files")
  val output = new CmdOption[String]("output", "A directory in which to write output to (optional)", false)
  val goldPaperMetaData = new CmdOption[String]("gold-paper-meta-data", "The file containing the ground truth paper meta data", true)
  val goldCitationEdges = new CmdOption[String]("gold-citation-edges", "The file containing the gold citation edges", true)
  val corefAlgorithms = new CmdOption[List[String]]("coref-algorithms", "The names of the coref algorithms to use",true)
}

/**
 * Runs the paper coreference experiment on the ACL data.  
 */
object ACLPaperCoreferenceExperiment {

  def main(args: Array[String]): Unit = {
    val opts = new ACLPaperCoreferenceExperimentOpts
    opts.parse(args)


    val formatType = FormatType(opts.formatType.value)

    val citationFiles: Iterable[File] =
      opts.input.value.flatMap((f) =>
        if (opts.inputType.value.equalsIgnoreCase("directory"))
          new File(opts.input.value.head).listFiles()
        else if (opts.inputType.value.equalsIgnoreCase("file of filenames")) {
          new BufferedReader(new FileReader(opts.input.value.head)).toIterator.map(new File(_)).toIterable
        } else if (opts.inputType.value.equalsIgnoreCase("file")) {
          Iterable(new File(f))
        } else
          throw new Exception(s"Unknown input type: ${opts.inputType.value}. Must be: directory,file of filenames, file")
      )

    val loader = Loader(formatType)
    val corefs = opts.corefAlgorithms.value.map(PaperCoref.apply)
    val experiment = new PaperCoreferenceExperiment(loader,citationFiles,opts.inputEncoding.value,opts.goldPaperMetaData.value,opts.goldCitationEdges.value,corefs)
    val result = experiment.run()
    if (opts.output.wasInvoked) {
      val outputDir = new File(opts.output.value)
      outputDir.mkdirs()
      val writer1 = new PrintWriter(new File(outputDir, "results.txt"), "UTF-8")
      writer1.println(result.map( (x) => x._1 + ":\n" + x._2 + "\n\n").mkString("--------------------------------------------\n\n"))
      writer1.close()
    }
  }
}

