package org.allenai.scholar.paper_coref.load

import java.io._
import java.util.UUID
import cc.factorie._

import org.allenai.scholar.paper_coref.data_structures.{Date, PaperMention, ParsedPaper}

object LoadCora extends MentionLoader {
  override val formatType: FormatType = CoraFormat

  override def fromFile(file: File, codec: String): Iterable[ParsedPaper] = throw new UnsupportedOperationException("not supported at this time")

  override def fromSeparateFiles(headerFile: File, referencesFile: File, codec: String): Option[ParsedPaper] = throw new UnsupportedOperationException("not supported at this time")


  private val authorFinder = AlmostXMLTag("author")
  private val titleFinder = AlmostXMLTag("title")
  private val yearFinder = AlmostXMLTag("year")
  private val booktitleFinder = AlmostXMLTag("booktitle")
  private val journalFinder = AlmostXMLTag("journal")

  override def loadMentionsFromFile(file: File, codec: String): Iterable[PaperMention] = {

    val contents = new BufferedReader(new InputStreamReader(new FileInputStream(file), codec)).toIterator.mkString(" ").split("<NEWREFERENCE>(\\d+)?").map(_.trim).filterNot(_.isEmpty)

    contents.map {
      stringMention =>
        val id = UUID.randomUUID().toString
        val goldId = stringMention.split("[\\s|<]").head
        val title = titleFinder.getFirstValue(stringMention)
        val authors = authorFinder.getValues(stringMention).toSet[String]
        val date = yearFinder.getFirstValue(stringMention).map(Date(_).year)
        val venue = Iterable(booktitleFinder, journalFinder).flatMap(_.getFirstValue(stringMention)).headOption
        val goldMention = PaperMention(goldId, authors, title.getOrElse(""), venue.getOrElse(""), date.getOrElse(""), goldId, false, None)
        val mention = PaperMention(id, authors, title.getOrElse(""), venue.getOrElse(""), date.getOrElse(""), goldId, false, Some(goldMention))
        mention
    }

  }


}


case class AlmostXMLTag(tagName: String) {

  val regex = s"<$tagName>(?:(?!<\\/$tagName>).)*<\\/$tagName>".r

  private val replacementRegex = s"<[\\/]*$tagName>"

  def getValues(almostXml: String): Iterable[String] = {
    regex.findAllIn(almostXml).map(_.replaceAll(replacementRegex, "").trim).toIterable
  }

  def getFirstValue(almostXML: String): Option[String] = {
    regex.findFirstIn(almostXML).map(_.replaceAll(replacementRegex, "").trim)
  }

} 