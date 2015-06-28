package org.allenai.scholar.paper_coref.process

import java.io.{File, PrintWriter}

import cc.factorie.util.DefaultCmdOptions
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.load.Loader

/**
 * The command line options  
 */
class WriteExtractionsToPaperMetadataJSONOpts extends DefaultCmdOptions {
  val formatType = new CmdOption[String]("format-type", "The format of the input, RPP, ParsCit, Grobid.",true)
  val input = new CmdOption[List[String]]("input", "Either a directory of files, a filename of files, or a list of files", false)
  val headers = new CmdOption[List[String]]("headers", "Either a directory of files, a filename of files, or a list of files containing header information", false)
  val references = new CmdOption[List[String]]("references", "Either a directory of files, a filename of files, or a list of files containing reference information", false)
  val inputType = new CmdOption[String]("input-type", "Directory, file of filenames, file", true)
  val inputEncoding = new CmdOption[String]("input-encoding", "UTF-8", "CODEC", "The encoding of the input files")
  val output = new CmdOption[String]("output", "An output directory", true)
  val outputEncoding = new CmdOption[String]("output-encoding", "UTF-8", "CODEC", "The encoding of the output files")
  val compact = new CmdOption[Boolean]("compact", true, "BOOLEAN", "Whether or not to use the compact JSON format. Default false")
}

/**
 * Serializing PaperMetadata data structures.  
 */
object WriteExtractionsToPaperMetadataJSON {

  def main(args: Array[String]): Unit = {
    val opts = new WriteExtractionsToPaperMetadataJSONOpts
    opts.parse(args)

    val loader = Loader(opts.formatType.value)

    assert((opts.input.wasInvoked || (opts.headers.wasInvoked && opts.references.wasInvoked)) && !(opts.input.wasInvoked && opts.headers.wasInvoked && opts.references.wasInvoked), "Either input or headers and references must be specified.")

    val papers = if (opts.input.wasInvoked) {
      val input = parseExperimentInput(opts.input.value,opts.inputType.value)
      loader.fromFiles(input,opts.inputEncoding.value)
    } else {
      val headers = parseExperimentInput(opts.headers.value,opts.inputType.value).groupBy(_.getNameWithoutExtension)
      val references = parseExperimentInput(opts.references.value,opts.inputType.value).groupBy(_.getNameWithoutExtension)
      val joined = headers.keySet.intersect(references.keySet).flatMap(fid => headers(fid).zip(references(fid))).toList
      loader.fromSeparateFiles(joined, opts.inputEncoding.value)
    }

    papers.map((c) => (c.self.foundInId,c.toPaperMetadata)).foreach{
      case (paperId,paper) =>
        val pw = new PrintWriter(new File(opts.output.value, paperId + ".json"),opts.outputEncoding.value)
        if (opts.compact.value)
          paper.foreach( (p) => pw.println(p.toJSON))
        else
          paper.foreach( (p) => pw.print(p.toJSONFormatted))
        pw.close()
    }

  }


}
