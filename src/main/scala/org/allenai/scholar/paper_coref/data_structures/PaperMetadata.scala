package org.allenai.scholar.paper_coref.data_structures

import java.io._

import cc.factorie._
import org.apache.commons.lang.StringEscapeUtils._

/**
 * @author John Sullivan
 */
case class GoldCitationDoc(doc:PaperMetadata, citations:Iterable[PaperMetadata])
case class PaperMetadata(id: String, title: String, venue: String,
                         year: Int, authors: List[String])

object PaperMetadata {

  def fromInputStream(is:InputStream):Iterable[PaperMetadata] =
    new BufferedReader(new InputStreamReader(is)).toIterator.grouped(5).map {
      case Seq(idLine, authorLine, titleLine, venueLine, dateLine) =>
        PaperMetadata(trim("id", idLine),
          unescapeXml(trim("title", titleLine)),
          unescapeXml(trim("venue", venueLine)),
          trim("year", dateLine).toInt,
          unescapeXml(trim("author", authorLine)).split(";").toList)
    }.toIterable

  def fromFile(filename:String):Iterable[PaperMetadata] = fromInputStream(new FileInputStream(filename))

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
