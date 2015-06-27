package org.allenai.scholar.paper_coref.data_structures

import java.io._

import cc.factorie._
import org.apache.commons.lang.StringEscapeUtils._

/**
 * Data structured used in evaluating the ACL experiment. 
 * @author John Sullivan
 */
case class GoldCitationDoc(doc:PaperMetadataWithId, citations:Iterable[PaperMetadataWithId])

/**
 * Following the PaperMetadata data structure in Meta-Eval with the added field of the paper id.
 * @param id - the paper id (typically ACL)
 * @param title - the title of the paper
 * @param venue - the venue
 * @param year - year (four digit number)
 * @param authors - the authors with the format: "lastname, firstname middlename"
 */
case class PaperMetadataWithId(id: String, title: String, venue: String,
                         year: Int, authors: List[String])


/**
 * Methods for loading PaperMetadataWithId data structures.
 */
object PaperMetadataWithId { //TODO: In the future, using JSON serialized formatting might be cleaner? - nm

  /**
   * Load PaperMetadataWithId from an input stream. 
   * @param is - input stream
   * @return PaperMetadataWithId objects
   */
  def fromInputStream(is:InputStream):Iterable[PaperMetadataWithId] =
    new BufferedReader(new InputStreamReader(is)).toIterator.grouped(5).map {
      case Seq(idLine, authorLine, titleLine, venueLine, dateLine) =>
        PaperMetadataWithId(trim("id", idLine),
          unescapeXml(trim("title", titleLine)),
          unescapeXml(trim("venue", venueLine)),
          trim("year", dateLine).toInt,
          unescapeXml(trim("author", authorLine)).split(";").toList)
    }.toIterable

  /**
   * Load PaperMetadataWithId from a file
   * @param filename
   * @return
   */
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

