package org.allenai.scholar.paper_coref

import java.io._
import cc.factorie._
import org.json4s._
import org.json4s.jackson.JsonMethods._

object ParsedPaper {
  def fromCitations(cits:Iterable[LocatedCitation]):ParsedPaper = {
    assert(cits.count(_.paperId.isDefined) == 1)
    ParsedPaper(cits.filter(_.paperId.isDefined).head, cits.filterNot(_.paperId.isDefined))
  }
}

case class ParsedPaper(self:LocatedCitation, bib:Iterable[LocatedCitation])

case class RawCitation(rawTitle:String, rawAuthors:List[String], date:String)

case class LocatedCitation(rawCitation:RawCitation, citingPaperId:Option[String], paperId:Option[String]) {
  lazy val foundInId = citingPaperId.getOrElse(paperId.get)
}

object LocatedCitation {

  implicit val formats = DefaultFormats

  def fromInputStream(is:InputStream):Iterable[LocatedCitation] =
    new BufferedReader(new InputStreamReader(is)).toIterator.map{ line =>
      parse(line).extract[LocatedCitation]
  }.toIterable

  def fromFile(filename:String):Iterable[LocatedCitation] = fromInputStream(new FileInputStream(filename))
}
