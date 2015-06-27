package org.allenai.scholar.paper_coref.data_structures


/**
 * A ParsedPaper is a representation of a scientific paper, it is represented by a LocatedCitation of the paper itself and
 * a collection of LocatedCitations for the papers cited by the paper. The fields of a ParsedPaper:
 * @param self - The reference representing the paper itself
 * @param bib - The papers referenced in this paper
 */
case class ParsedPaper(self:LocatedCitation, bib:Iterable[LocatedCitation]) extends JSONSerializable{
  override def toString = s"(ParsedPaper(${self.paperId.getOrElse("UnknownId")}), self: ${self.toString}, bib: ${bib.map(_.toString).mkString(", ")})"
  def toPaperMetadata: Iterable[PaperMetadata] = (Iterable(self) ++ bib).map(_.rawCitation).map(PaperMetadata.fromRawCitation)
}


/**
 * Convenience methods for the ParsedPaper data structure. *
 */
object ParsedPaper {
  
  /**
   * Given a group of LocatedCitations create a ParsedPaper object. Exactly one of the citations
   * must have the "paperId" field defined, denoting it is the header of the ParsedPaper. The remaining
   * citations make up the references. An error is thrown if zero or more than one citation has the paperId 
   * field defined. 
   * @param cits - the citations 
   * @return - a ParsedPaper
   */
  def fromCitations(cits:Iterable[LocatedCitation]):ParsedPaper = {
    assert(cits.count(_.paperId.isDefined) == 1)
    ParsedPaper(cits.filter(_.paperId.isDefined).head, cits.filterNot(_.paperId.isDefined))
  }

  /**
   * Same as fromCitations except it returns None in cases where fromCitations would have thrown an error.
   * @param cits - the citations
   * @return - a ParsedPaper or None if zero or more than one citation has the paperId
   */
  def fromCitationsSafe(cits:Iterable[LocatedCitation]): Option[ParsedPaper] = {
    if (cits.count(_.paperId.isDefined) == 1)
      Some(ParsedPaper(cits.filter(_.paperId.isDefined).head, cits.filterNot(_.paperId.isDefined)))
    else
      None
  }
}