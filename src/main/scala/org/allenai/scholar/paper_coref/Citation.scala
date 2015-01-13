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
case class Citation(rawAuthor:String, rawTitle:String, rawCoAuthors:List[String], date:String, paperId:Option[String], citingPaperId:Option[String], goldLabel:Option[String]) {
  lazy val paperOrCitingId:String = paperId.getOrElse(citingPaperId.get)
  implicit val jf = DefaultFormats

  def withGoldLabel(gold:String):Citation = {
    assert (goldLabel.isEmpty)
    Citation(rawAuthor, rawTitle, rawCoAuthors, date, paperId, citingPaperId, Some(gold))
  }

  lazy val toJsonString = write(this)
}

case class ParsedPaper(selfCit:Citation, bib:List[Citation])

object ParsedPaper {
  implicit val jf = DefaultFormats
  def loadFromDir(dirName:String):Iterable[ParsedPaper] = new File(dirName)
    .listFiles().flatMap(f => new BufferedReader(new InputStreamReader(new FileInputStream(f))).toIterator)
    .map(line => parse(line).extract[Citation])
    .groupBy(_.paperOrCitingId).flatMap {case (_, cits) =>
      val paperCits = cits.filter(_.paperId.isDefined)
      if(paperCits.size == 1) {
        val paperCit = paperCits.head
        Some(ParsedPaper(paperCit, cits.filter(_.citingPaperId.isDefined).toList))
      } else {
        println("WARNING: Paper Id %s had no root paper".format(cits.head.paperOrCitingId))
        None
      }
  }
}
