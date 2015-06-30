package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.data_structures.{Author, Date, RawCitation}

import scala.xml.{Elem, NodeSeq}

/**
 * Loader for the RPP data
 */
object LoadRPP extends XMLLoader{
  
  val formatType = RPPFormat
  
  override def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \\ "header"
    val title = (header \\ "title").map(_.text).headOption.getOrElse("") // The first appearance of a title or else empty
    val authors =getAuthors(header)
    val date = Date((header \\ "date").map(_.text).headOption.getOrElse("")).year
    val venue = getVenue(header)
    val citation = RawCitation(title,authors.toList,date,venue)
    if (citation.isEmpty) None else Some(citation)
  }

  private def getAuthors(nodeSeq: NodeSeq) =
    (nodeSeq \\ "authors").map(_ \\ "person").map(getAuthor)

  private def getAuthor(nodeSeq: NodeSeq) = {
    val lastName = (nodeSeq \\ "person-last").map(_.text).headOption.getOrElse("")
    val firstName = (nodeSeq \\ "person-first").map(_.text).headOption.getOrElse("")
    val middleName = (nodeSeq \\ "person-middle").map(_.text)
    Author(firstName,middleName,lastName).formattedString
  }

  override def loadReferences(xml: Elem): Iterable[RawCitation] = {
    (xml \\ "references" \\ "reference").flatMap(loadReference)
  }
  
  def getVenue(nodeSeq: NodeSeq) =
    Seq(nodeSeq \\ "journal", nodeSeq \\ "booktitle", nodeSeq \\ "institution").map(_.text).headOption.getOrElse("")

  override def loadReference(xml: NodeSeq): Option[RawCitation] = {
    val title = (xml \\ "title").map(_.text).headOption.getOrElse("")
    val authors = (xml \\ "authors" \\ "person").map(_ \ "_").map(_.map(_.text.trim).mkString(" ")).filterNot(_.isEmpty)
    val date = Date((xml \\ "date").map(_.text).headOption.getOrElse("")).year
    val venue = getVenue(xml)
    val citation = RawCitation(title,authors.toList,date,venue)
    if (citation.isEmpty) None else Some(citation)
  }
}
