package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.data_structures.{Date, Author, RawCitation}

import scala.xml.{Elem, NodeSeq}

object LoadGrobid extends XMLLoader{
  
  val formatType = GrobidFormat

  def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \ "teiHeader"
    val title = (header \\ "titleStmt" \\ "title").map(_.text).headOption.getOrElse("")
    val authors = getAuthors(header)
    val date = getDate(header)
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
  
  private def getDate(nodeSeq: NodeSeq) = Date((nodeSeq \\  "publicationStmt"  \\ "date").map((p) => ((p \\ "@when").text, (p \\ "@type").text)).filter(_._2 == "published").map(_._1).headOption.getOrElse("")).year
  
  private def getAuthors(nodeSeq: NodeSeq) =
    (nodeSeq \\ "author").map(_ \\ "persName").map(getAuthor)

  private def getAuthor(nodeSeq: NodeSeq) = {
    val lastName = (nodeSeq \\ "surname").map(_.text).headOption.getOrElse("")
    val firstAndMiddle = (nodeSeq \\ "forename").map((n) => (n \\ "@type").text -> n.text).groupBy(_._1).mapValues(_.map(_._2))
    Author(firstAndMiddle.getOrElse("first",Seq("")).head, firstAndMiddle.getOrElse("middle",Seq()), lastName).formattedString
  }
  
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
