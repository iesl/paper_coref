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
    .groupBy(_.paperOrCitingId).map {case (_, cits) =>
      val paperCits = cits.filter(_.paperId.isDefined)
      assert(paperCits.size == 1, "Expected a single paper copy, instead found: %s\nand:%s".format(paperCits.mkString("\n"), cits.mkString("\n")))
      val paperCit = paperCits.head
      ParsedPaper(paperCit, cits.filter(_.citingPaperId.isDefined).toList)
  }
}
