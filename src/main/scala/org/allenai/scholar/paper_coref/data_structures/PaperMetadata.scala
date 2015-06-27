package org.allenai.scholar.paper_coref.data_structures

import cc.factorie._

/**
 * Follows the same schema as the PaperMetadata data structure in Meta-Eval
 * Meant to facilitate input from Meta-Eval processed data or at least make 
 * the interface between the two projects smoother. 
 * @param title - the title of the paper
 * @param venue - the venue of the paper
 * @param year - the year (four digit number) of the paper
 * @param authors - the authors with the format "lastname, firstname middlename"
 */
case class PaperMetadata(title: String, venue: String, year: Int, authors: List[String]) extends JSONSerializable

/**
 * Convenience methods for PaperMetadata 
 */
object PaperMetadata {

  /**
   * Convert the given RawCitation into a PaperMetadata object.
   * @param rawCitation - the raw citation
   * @return - the corresponding PaperMetadata object
   */
  def fromRawCitation(rawCitation: RawCitation): PaperMetadata = PaperMetadata(rawCitation.rawTitle,rawCitation.venue,rawCitation.date.toIntSafe.getOrElse(0),rawCitation.rawAuthors)

}
