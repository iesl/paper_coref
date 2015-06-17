package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.RawCitation

import scala.xml.{Elem, NodeSeq}

object LoadRPP extends XMLLoader{
  
  val formatType = RPP
  
  override def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \\ "header"
    val title = (header \\ "title").map(_.text).headOption.getOrElse("") // The first appearance of a title or else empty
    val authors = (header \\ "authors" \\ "person").map(_ \ "_").map(_.map(_.text.trim).mkString(" ")).filterNot(_.isEmpty) // Each
    val date = (header \\ "date").map(_.text).headOption.getOrElse("")
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
  

  override def loadReferences(xml: Elem): Iterable[RawCitation] = {
    (xml \\ "references" \\ "reference").flatMap(loadReference)
  }

  override def loadReference(xml: NodeSeq): Option[RawCitation] = {
    val title = (xml \\ "title").map(_.text).headOption.getOrElse("")
    val authors = (xml \\ "authors" \\ "person").map(_ \ "_").map(_.map(_.text.trim).mkString(" ")).filterNot(_.isEmpty)
    val date = (xml \\ "date").map(_.text).headOption.getOrElse("")
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
}
