package org.allenai.scholar.paper_coref.data_structures

import java.io._

import cc.factorie._
import org.apache.commons.lang.StringEscapeUtils._

/**
 * @author John Sullivan
 */
case class GoldCitationDoc(doc:PaperMetadataWithId, citations:Iterable[PaperMetadataWithId])

case class PaperMetadataWithId(id: String, title: String, venue: String,
                         year: Int, authors: List[String])

object PaperMetadataWithId {

  def fromInputStream(is:InputStream):Iterable[PaperMetadataWithId] =
    new BufferedReader(new InputStreamReader(is)).toIterator.grouped(5).map {
      case Seq(idLine, authorLine, titleLine, venueLine, dateLine) =>
        PaperMetadataWithId(trim("id", idLine),
          unescapeXml(trim("title", titleLine)),
          unescapeXml(trim("venue", venueLine)),
          trim("year", dateLine).toInt,
          unescapeXml(trim("author", authorLine)).split(";").toList)
    }.toIterable

  def fromFile(filename:String):Iterable[PaperMetadataWithId] = fromInputStream(new FileInputStream(filename))

  // shamelessly yoinked from org.allenai.scholar.PaperMetadataFileParser
  private def trim(keyname: String, string: String): String = {

    val prefix = s"${keyname} = {"
    require(
      string.startsWith(prefix),
      s"'${string}' for key ${keyname} doesn't startwith '${prefix}'"
    )

    var s = string.drop(prefix.length)

    // Except some lines don't have the final '}'
    if (s.endsWith("}")) s = s.dropRight(1)

    s
  }
}

case class PaperMetadata(title: String, venue: String, year: Int, authors: List[String]) extends JSONSerializable

object PaperMetadata {
  
  def fromRawCitation(rawCitation: RawCitation): PaperMetadata = PaperMetadata(rawCitation.rawTitle,rawCitation.venue,rawCitation.date.toIntSafe.getOrElse(0),rawCitation.rawAuthors)
  
}