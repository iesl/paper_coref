package org.allenai.scholar.paper_coref

import cc.factorie._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._
import org.json4s.DefaultFormats
import java.io.{InputStreamReader, FileInputStream, BufferedReader, File}


/**
 * @author John Sullivan
 */
case class AuthorCitation(rawAuthor:String, rawTitle:String, rawCoAuthors:List[String], date:String, paperId:Option[String], citingPaperId:Option[String], goldLabel:Option[String]) {
  lazy val paperOrCitingId:String = paperId.getOrElse(citingPaperId.get)
  lazy val authorSet = (rawAuthor :: rawCoAuthors).toSet

  lazy val toCitation = Citation(rawTitle, authorSet.toList, date, paperOrCitingId, paperId.isDefined, goldLabel)
}

case class Citation(rawTitle:String, rawAuthors:List[String], date:String, foundInPaperId:String, isPaper:Boolean, goldLabel:Option[String]) {
  def withGoldLabel(gold:String):Citation = {
    assert(goldLabel.isEmpty)
    Citation(rawTitle, rawAuthors, date, foundInPaperId, isPaper, Some(gold))
  }

  implicit val jf = DefaultFormats
  lazy val toJsonString = write(this)
}

object CollapseCitations {
  implicit val jf = DefaultFormats
  def fromDir(dirname:String):Iterable[Citation] = new File(dirname)
    .listFiles().flatMap(f => new BufferedReader(new InputStreamReader(new FileInputStream(f))).toIterator)
    .map(line => parse(line).extract[AuthorCitation].toCitation).toSet
}

case class ParsedPaper(selfCit:Citation, bib:List[Citation])

object ParsedPaper {
  implicit val jf = DefaultFormats
  def fromCitations(cits:Iterable[Citation]):Iterable[ParsedPaper] = cits.groupBy(_.foundInPaperId)
    .flatMap{ case (_, cs) =>
    val paperCits = cits.filter(_.isPaper)
    if(paperCits.size == 1) {
      val paperCit = paperCits.head
      Some(ParsedPaper(paperCit, cits.filter(c => !c.isPaper).toList))
    } else {
      println("WARNING: Paper Id %s had no root paper".format(cits.head.foundInPaperId))
      None
    }
  }

}
