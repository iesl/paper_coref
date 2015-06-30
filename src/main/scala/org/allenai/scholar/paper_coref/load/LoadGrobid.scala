package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.data_structures.{Date, Author, RawCitation}

import scala.xml.{Elem, NodeSeq}

/**
 * Loader for the Grobid data. *
 */
object LoadGrobid extends XMLLoader{
  
  val formatType = GrobidFormat

  def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \ "teiHeader"
    val title = (header \\ "titleStmt" \\ "title").map(_.text).headOption.getOrElse("")
    val authors = getAuthors(header)
    val date = getDate(header  \\  "publicationStmt")
    val venue = (header \\ "sourceDesc" \\ "monogr" \\ "title").map(_.text).headOption.getOrElse("")
    val citation = RawCitation(title,authors.toList,date, venue)
    if (citation.isEmpty) None else Some(citation)
  }
  
  private def getDate(nodeSeq: NodeSeq) = Date((nodeSeq  \\ "date").map((p) => ((p \\ "@when").text, (p \\ "@type").text)).filter(_._2 == "published").map(_._1).headOption.getOrElse("")).year
  
  private def getAuthors(nodeSeq: NodeSeq) =
    (nodeSeq \\ "author").map(_ \\ "persName").map(getAuthor)

  private def getAuthor(nodeSeq: NodeSeq) = {
    val lastName = (nodeSeq \\ "surname").map(_.text).headOption.getOrElse("")
    val firstAndMiddle = (nodeSeq \\ "forename").map((n) => (n \\ "@type").text -> n.text).groupBy(_._1).mapValues(_.map(_._2))
    Author(firstAndMiddle.getOrElse("first",Seq("")).head, firstAndMiddle.getOrElse("middle",Seq()), lastName).formattedString
  }
  

  def loadReferences(xml: Elem):Iterable[RawCitation] =
    (xml \\ "listBibl" \\ "biblStruct").flatMap(loadReference)
  
  def loadReference(biblStruct: NodeSeq): Option[RawCitation] = {
    val title = getReferenceTitle(biblStruct)
    val authors = getReferenceAuthors(biblStruct)
    val date = getDate(biblStruct) //(biblStruct \\ "date" \\ "@when").text ?
    val venue = (biblStruct \\ "monogr" \\ "title").map(_.text).headOption.getOrElse("")
    val citation = RawCitation(title,authors.toList,date, venue)
    if (citation.isEmpty) None else Some(citation)
  }
  
  private def getReferenceTitle(nodeSeq: NodeSeq) = {
    ((nodeSeq \\ "analytic" \\ "title").filter((p) => (p \\ "@type").text == "main").map(_.text) ++
      (nodeSeq \\ "monogr" \\ "title").map(_.text)).headOption.getOrElse("")
  }

  private def getReferenceAuthors(nodeSeq: NodeSeq) = {
    Seq(nodeSeq \\ "analytic" \\ "author", nodeSeq \\ "monogr" \\ "author").map(_.map(_ \\ "persName").map(getAuthor)).find(_.nonEmpty).getOrElse(Seq.empty)
  }
  
}
