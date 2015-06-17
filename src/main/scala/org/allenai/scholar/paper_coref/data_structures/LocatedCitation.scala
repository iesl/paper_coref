package org.allenai.scholar.paper_coref.data_structures

import java.io._

import cc.factorie._
import org.json4s._
import org.json4s.jackson.JsonMethods._

// TODO: This has issues with EmptyValueStrategy
trait JSONSerializable {
  def toJSON: String
}

object JSONSerializable {

  def writeJSON(citations: Iterator[JSONSerializable], file: File, codec: String = "UTF-8", bufferSize: Int = 10) = {
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), codec))
    val citationStrings = citations.map(_.toJSON)
    if (citationStrings.hasNext)
      writer.write(citationStrings.next())
    if (citationStrings.hasNext)
      citationStrings.grouped(bufferSize).foreach {
        group =>
          group.foreach {
            citation =>
              writer.newLine()
              writer.write(citation)
          }
          writer.flush()
      }
  }
}

object ParsedPaper {
  def fromCitations(cits:Iterable[LocatedCitation]):ParsedPaper = {
    assert(cits.count(_.paperId.isDefined) == 1)
    ParsedPaper(cits.filter(_.paperId.isDefined).head, cits.filterNot(_.paperId.isDefined))
  }
}

/**
 * A ParsedPaper is representation of a scientific paper, it is represented by a LocatedCitation of the paper itself and
 * * a collection of LocatedCitations for the papers cited by the paper. The fields of a ParsedPaper:
 * @param self - The reference representing the paper itself
 * @param bib - The papers referenced in this paper
 */
case class ParsedPaper(self:LocatedCitation, bib:Iterable[LocatedCitation]) extends JSONSerializable{
  override def toString = s"(ParsedPaper(${self.paperId.getOrElse("UnknownId")}), self: ${self.toString}, bib: ${bib.map(_.toString).mkString(", ")})"
  def toJSON: String = compact(render(Extraction.decompose(this)(DefaultFormats)))
}

/**
 *A RawCitation is a representation of a paper. It could either be the header of a paper or an entry in the reference section of the paper. The fields of a RawCitation are:
 * @param rawTitle - The title of the paper exactly in the way it was extracted from the PDF
 * @param rawAuthors - A list of authors. The author names are not structured, e.g. first and last names are not annotated or differentiated
 * @param date - The date exactly in the way it was extracted from the PDF, e.g. without formatting
 */
case class RawCitation(rawTitle:String, rawAuthors:List[String], date:String) {
  override def toString = s"(RawCitation, rawTitle: $rawTitle, rawAuthors: ${rawAuthors.mkString(", ")}, date: $date)"
  def isEmpty: Boolean = rawTitle.isEmpty && rawAuthors.isEmpty && date.isEmpty 
}

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
  def toJSON: String = compact(render(Extraction.decompose(this)(DefaultFormats)))
}

object LocatedCitation {

  implicit val formats = DefaultFormats

  def fromInputStream(is:InputStream):Iterable[LocatedCitation] =
    new BufferedReader(new InputStreamReader(is)).toIterator.map{ line =>
      parse(line).extract[LocatedCitation]
  }.toIterable

  def fromFile(filename:String):Iterable[LocatedCitation] = fromInputStream(new FileInputStream(filename))
}
