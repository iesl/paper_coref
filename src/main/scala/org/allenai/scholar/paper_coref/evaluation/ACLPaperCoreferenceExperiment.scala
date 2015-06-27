package org.allenai.scholar.paper_coref.evaluation

import java.io.{File, PrintWriter}

import cc.factorie.util.DefaultCmdOptions
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.coreference.PaperCoref
import org.allenai.scholar.paper_coref.load.{FormatType, Loader}

/**
 * Command line options for the paper coreference experiment on ACL data 
 */
class ACLPaperCoreferenceExperimentOpts extends DefaultCmdOptions {
  val formatType = new CmdOption[String]("format-type", "The format of the input, RPP, ParsCit, Grobid.",true)
  val input = new CmdOption[List[String]]("input", "Either a directory of files, a filename of files, or a list of files", false)
  val headers = new CmdOption[List[String]]("headers", "Either a directory of files, a filename of files, or a list of files containing header information", false)
  val references = new CmdOption[List[String]]("references", "Either a directory of files, a filename of files, or a list of files containing reference information", false)
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
    val loader = Loader(formatType)
    val corefs = opts.corefAlgorithms.value.map(PaperCoref.apply)
    
    assert((opts.input.wasInvoked || (opts.headers.wasInvoked && opts.references.wasInvoked)) && !(opts.input.wasInvoked && opts.headers.wasInvoked && opts.references.wasInvoked), "Either input or headers and references must be specified.")

    val experiment = {
      if (opts.input.wasInvoked) {
        val citationFiles: Iterable[File] = parseExperimentInput(opts.input.value,opts.inputType.value)
        new PaperCoreferenceExperiment(loader, citationFiles, opts.inputEncoding.value, opts.goldPaperMetaData.value, opts.goldCitationEdges.value, corefs)
      } else {
        val headers = parseExperimentInput(opts.headers.value,opts.inputType.value).groupBy(_.getNameWithoutExtension)
        val references = parseExperimentInput(opts.references.value,opts.inputType.value).groupBy(_.getNameWithoutExtension)
        val joined = headers.keySet.intersect(references.keySet).flatMap(fid => headers(fid).zip(references(fid))).toList
        new PaperCoreferenceExperiment(loader.fromSeparateFiles(joined,opts.inputEncoding.value),opts.inputEncoding.value, opts.goldPaperMetaData.value, opts.goldCitationEdges.value, corefs)
      }
    }

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

