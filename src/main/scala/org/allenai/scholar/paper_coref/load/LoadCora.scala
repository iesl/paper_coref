package org.allenai.scholar.paper_coref.load

import java.io._
import java.util.UUID
import cc.factorie._

import org.allenai.scholar.paper_coref.data_structures.{Date, PaperMention, ParsedPaper}

/**
 * Loader for the Cora dataset.  
 */
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

/**
 * Helper class for loading Cora. Uses regexes to match the XML like Cora formatting. 
 * @param tagName - the name of the tag
 */
case class AlmostXMLTag(tagName: String) {

  /**
   * The regex that is used to find the value(s) for the tag. 
   */
  val regex = s"<$tagName>(?:(?!<\\/$tagName>).)*<\\/$tagName>".r

  private val replacementRegex = s"<[\\/]*$tagName>"

  /**
   * Returns all of the values associated with this tag.
   * @param almostXml - the string to extract values from
   * @return - the values associated with the tag
   */
  def getValues(almostXml: String): Iterable[String] = {
    regex.findAllIn(almostXml).map(_.replaceAll(replacementRegex, "").trim).toIterable
  }

  /**
   * Returns the first value associated with this tag.
   * @param almostXML - the string to extract values from
   * @return - the values associated with the tag.
   */
  def getFirstValue(almostXML: String): Option[String] = {
    regex.findFirstIn(almostXML).map(_.replaceAll(replacementRegex, "").trim)
  }

} 