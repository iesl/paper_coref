package org.allenai.scholar.paper_coref.load

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}

import cc.factorie._
import org.allenai.scholar.paper_coref.data_structures.ParsedPaper
import org.json4s.NoTypeHints
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization

/**
 * Loader for JSON serialized ParsedPaper data. 
 * Note that data must be stored such that each JSON
 * object appears on a single line. 
 * TODO: I don't think that this is supported?
 */
object LoadParsedPaper extends Loader {
  override val formatType: FormatType = ParsedPaperFormat

  implicit val formats = Serialization.formats(NoTypeHints)

  override def fromFile(file: File, codec: String): Iterable[ParsedPaper] = new BufferedReader(new InputStreamReader(new FileInputStream(file),codec)).toIterator.map{ line =>
    parse(line).extract[ParsedPaper]
  }.toIterable

  override def fromSeparateHeaderAndReferenceFile(headerFile: File, referencesFile: File, codec: String): Option[ParsedPaper] = throw new UnsupportedOperationException
}
