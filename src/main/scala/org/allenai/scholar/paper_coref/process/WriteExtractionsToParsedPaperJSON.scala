package org.allenai.scholar.paper_coref.process

import java.io.{PrintWriter, FileReader, BufferedReader, File}

import cc.factorie.util.DefaultCmdOptions
import cc.factorie._
import org.allenai.scholar.paper_coref.load.Loader

class WriteExtractionsToParsedPaperJSONOpts extends DefaultCmdOptions {
  val formatType = new CmdOption[String]("format-type", "The format of the input, RPP, ParsCit, Grobid.",true)
  val input = new CmdOption[List[String]]("input", "Either a directory of files, a filename of files, or a list of files", true)
  val inputEncoding = new CmdOption[String]("input-encoding", "UTF-8", "CODEC", "The encoding of the input files")
  val output = new CmdOption[String]("output", "The output directory", false)
  val outputEncoding = new CmdOption[String]("output-encoding", "UTF-8", "CODEC", "The encoding of the output files")
  val compact = new CmdOption[Boolean]("compact", true, "BOOLEAN", "Whether or not to use the compact JSON format. Default false")
}

object WriteExtractionsToParsedPaperJSON {

  def main(args: Array[String]): Unit = {
    val opts = new WriteExtractionsToParsedPaperJSONOpts
    opts.parse(args)

    val citationFiles: Iterable[File] =  if (opts.input.value.length == 1) {
      if (new File(opts.input.value.head).isDirectory)
        new File(opts.input.value.head).listFiles()
      else
        new BufferedReader(new FileReader(opts.input.value.head)).toIterator.map(new File(_)).toIterable
    } else {
      opts.input.value.map(new File(_))
    }
    val loader = Loader(opts.formatType.value)

    new File(opts.output.value).mkdirs()
    val papers = loader.fromFiles(citationFiles)
    papers.foreach{ 
      paper =>
        val pw = new PrintWriter(new File(opts.output.value,paper.self.foundInId + ".json"), opts.outputEncoding.value)
        if (opts.compact.value)
          pw.println(paper.toJSON)
        else
          pw.print(paper.toJSONFormatted)
        pw.close()
    }
    
  }
  
  
}