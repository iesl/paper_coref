package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.data_structures.RawCitation

import scala.xml.{Elem, NodeSeq}

object LoadGrobid extends XMLLoader{
  
  val formatType = GrobidFormat

  def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \ "teiHeader"
    val title = (header \\ "titleStmt" \\ "title").map(_.text).headOption.getOrElse("")
    val authors = getAuthors(header)
    val date = (header \\ "publicationStmt"  \\ "date").map(_.text).headOption.getOrElse("") // TODO: There seems to be several options here. Grobid even seems to do some cleaning of this field in other places.
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
  
  private def getAuthors(nodeSeq: NodeSeq) = (nodeSeq \\ "author").map(_ \\ "persName").map(_ \ "_").map(_.map(_.text.trim).mkString(" ")).filterNot(_.isEmpty)
  
  def loadReferences(xml: Elem):Iterable[RawCitation] =
    (xml \\ "biblStruct").flatMap(loadReference)

  
  def loadReference(biblStruct: NodeSeq): Option[RawCitation] = {
    val title = (biblStruct \\ "analytic" \\ "title").text
    val authors = getAuthors(biblStruct)
    val date = (biblStruct \\ "date" \\ "@when").text
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
  
}
