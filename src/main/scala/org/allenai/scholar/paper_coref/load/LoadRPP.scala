package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.RawCitation

import scala.xml.{Elem, NodeSeq}

object LoadRPP extends XMLLoader{
  override def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \\ "header"
    val title = (header \\ "header-title").map(_.text).headOption.getOrElse("") 
    val authors = (header \\ "header-author").map(_.text)
    val date = (header \\ "header-date").map(_.text).headOption.getOrElse("")
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }

  override def loadReferences(xml: Elem): Iterable[RawCitation] = {
    (xml \\ "references" \\ "reference").flatMap(loadReference)
  }

  override def loadReference(xml: NodeSeq): Option[RawCitation] = {
    val title = (xml \\ "ref-title").map(_.text).headOption.getOrElse("")
    val authors = (xml \\ "ref-person").map(_.text)
    val date = (xml \\ "ref-date").map(_.text).headOption.getOrElse("")
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
}
