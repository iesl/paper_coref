package org.allenai.scholar.paper_coref.load

import org.allenai.scholar.paper_coref.{RawCitation,StringExtras}

import scala.xml.{Elem, NodeSeq}

object LoadParsCit extends XMLLoader{

  def loadHeader(xml: Elem): Option[RawCitation] = {

    val nonCitations = (xml \\ "algorithm").filter((p) => (p \\ "citationList").isEmpty)
    val title = mostConfidentValue(nonCitations, "title").getOrElse("")
    val authors = mostConfidentValues(nonCitations, "author")
    val date = mostConfidentValue(nonCitations, "date").getOrElse("")
    val citation = RawCitation(title.removeNewlines, authors.map(_.removeNewlines).toList, date.removeNewlines)
    if (citation.isEmpty) None else Some(citation)
  }
  
  def loadReferences(xml: Elem): Iterable[RawCitation] =
    (xml \\ "algorithm" \\ "citationList" \\ "citation").flatMap(loadReference)

  def loadReference(xml: NodeSeq): Option[RawCitation] = {
    val title = mostConfidentValue(xml, "title").getOrElse("")
    val authors = mostConfidentValues(xml, "author")
    val date = mostConfidentValue(xml, "date").getOrElse("")
    val citation = RawCitation(title.removeNewlines, authors.map(_.removeNewlines).toList, date.removeNewlines)
    if (citation.isEmpty) None else Some(citation)
  }
  
  def mostConfidentValue(xml: NodeSeq, field: String, confidenceFieldName: String = "@confidence", defaultValue: String = "0.0") =
   valuesWithConfidence(xml,field,confidenceFieldName,defaultValue).sortBy(-_._2).map(_._1).headOption

  def valuesWithConfidence(xml: NodeSeq, field: String, confidenceFieldName: String = "@confidence", defaultValue: String = "0.0") =
    (xml \\ field).map((p) => (p.text.trim, (p \\ confidenceFieldName).map(_.text).headOption.getOrElse(defaultValue).toDouble))
  
  def mostConfidentValues(xml: NodeSeq, field: String, confidenceFieldName: String = "@confidence", defaultValue: String = "0.0") = {
    val confidentValues = valuesWithConfidence(xml,field,confidenceFieldName,defaultValue)
    val maxConfidence = confidentValues.map(_._2).headOption.getOrElse(0.0)
    confidentValues.filter(_._2 == maxConfidence).map(_._1)
  }

}
