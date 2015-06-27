package org.allenai.scholar.paper_coref.data_structures

import java.io._

import cc.factorie._
import org.json4s._
import org.json4s.jackson.JsonMethods._


/**
 * A LocatedCitation represents a "mention" of a paper. It is a RawCitation that also contains either the unique id of the paper represented by the "mention" or the unique id of the paper which contains this citation in its reference.
 * @param rawCitation - the citation itself
 * @param citingPaperId - the unique id of the paper which contains this citation in its reference
 * @param paperId - the unique id of the paper which is represented in the rawCitation
 */
case class LocatedCitation(rawCitation:RawCitation, citingPaperId:Option[String], paperId:Option[String]) extends JSONSerializable {
  lazy val foundInId = citingPaperId.getOrElse(paperId.get)
  override def toString = s"(LocatedCitation, rawCitation:${rawCitation.toString}, citingPaperId: $citingPaperId, paperId: $paperId)"
  def isEmpty: Boolean = rawCitation.isEmpty
}

/**
 * Methods for loading located citations 
 */
object LocatedCitation {

  implicit val formats = DefaultFormats

  /**
   * Load the located citations from an input stream  
   * @param is - the input stream
   * @return located citations
   */
  def fromInputStream(is:InputStream):Iterable[LocatedCitation] =
    new BufferedReader(new InputStreamReader(is)).toIterator.map{ line =>
      parse(line).extract[LocatedCitation]
  }.toIterable

  /**
   * Load the citations from the file
   * @param filename - the file to load from
   * @return located citations
   */
  def fromFile(filename:String):Iterable[LocatedCitation] = fromInputStream(new FileInputStream(filename))
}
