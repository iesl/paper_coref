package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.RawCitation

import scala.xml.{Elem, NodeSeq}

object LoadParsCit extends XMLLoader{

  def loadHeader(xml: Elem): Option[RawCitation] = {
    val maybeTitleIndex = (xml \\ "algorithm").map((p) => p  \\ "title").zipWithIndex.dropWhile(_._1.isEmpty).headOption
    if (maybeTitleIndex.isDefined) {
      val titleIndex = maybeTitleIndex.get._2
      val title = (xml \\ "algorithm").map((p) => p \\ "title").drop(titleIndex).map(_.head).head.text // Take the first title listed of the first algorithm block which contains a title.
      val authors = (xml \\ "algorithm").map((p) => (p \\ "author").map(_.text)).drop(titleIndex).head // Take all of the authors listed in the algorithm block containing the title
      val maybeDate = (xml \\ "algorithm").map((p) => p \\ "date").drop(titleIndex).flatMap(_.headOption).headOption // Take the date from the block that contains the author and title
      val date = if (maybeDate.isDefined) maybeDate.get.text else ""
      val citation = RawCitation(title, authors.toList, date)
      if (citation.isEmpty) None else Some(citation)
    } else
      None
  }
  
  def loadReferences(xml: Elem): Iterable[RawCitation] =
    (xml \\ "algorithm" \\ "citationList" \\ "citation").flatMap(loadReference)

  
  def loadReference(xml: NodeSeq): Option[RawCitation] = {
    val maybeTitle = (xml  \\ "title").headOption
    val authors = (xml \\ "author").map(_.text)
    val maybeDate = (xml \\ "date").headOption
    val title = if (maybeTitle.isDefined) maybeTitle.get.text else ""
    val date = if (maybeDate.isDefined) maybeDate.get.text else ""
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }

}
