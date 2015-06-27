package org.allenai.scholar.paper_coref.load

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}

import cc.factorie._
import org.allenai.scholar.paper_coref.data_structures.{LocatedCitation, ParsedPaper}
import org.json4s._
import org.json4s.jackson.JsonMethods._

object LoadLocatedCitations extends Loader {
  
  override val formatType: FormatType = LocatedCitationFormat

  implicit val formats = DefaultFormats

  override def fromFile(file: File, codec: String): Iterable[ParsedPaper] =
    getLocatedCitations(file,codec).groupBy(_.foundInId).map(_._2).flatMap(ParsedPaper.fromCitationsSafe)

  override def fromSeparateFiles(headerFile: File, referencesFile: File, codec: String): Option[ParsedPaper] =  {
    val headers = getLocatedCitations(headerFile,codec)
    if (headers.size > 1)
      println(s"[LoadLocatedCitations] WARNING: The file ${headerFile.getName} has more than one LocatedCitation. The first one will be used as the header.")
    if (headers.size == 0)
      println(s"[LoadLocatedCitations] WARNING: No header specified in file, no ParsedPaper will be loaded.")
    val header = headers.headOption
    val references = getLocatedCitations(referencesFile,codec)
    if (header.isDefined)
      Some(ParsedPaper(header.get,references))
    else
      None
  }

  private def getLocatedCitations(file:File,codec:String):Iterable[LocatedCitation] =
    new BufferedReader(new InputStreamReader(new FileInputStream(file),codec)).toIterator.map{ line =>
      parse(line).extract[LocatedCitation]
    }.toIterable
  
}
