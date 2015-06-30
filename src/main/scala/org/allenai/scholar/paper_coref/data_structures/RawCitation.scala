package org.allenai.scholar.paper_coref.data_structures

/**
 *A RawCitation is a representation of a paper. It could either be the header of a paper or an entry in the reference section of the paper. The fields of a RawCitation are:
 * @param rawTitle - The title of the paper as it was extracted
 * @param rawAuthors - A list of authors. The author names are not necessarily structured, e.g. first and last names are not annotated or differentiated
 * @param date - The date, not necessarily structured
 * @param venue - The venue the paper appeared in.
 */
case class RawCitation(rawTitle:String, rawAuthors:List[String], date:String, venue: String) {
  override def toString = s"(RawCitation, rawTitle: $rawTitle, rawAuthors: ${rawAuthors.mkString(", ")}, date: $date, venue: $venue)"
  def isEmpty: Boolean = rawTitle.isEmpty && rawAuthors.isEmpty && date.isEmpty && venue.isEmpty
}

/**
 * Additional methods for RawCitation.  
 */
object RawCitation {

  /**
   * Alternate constructor for RawCitation. Sets the value of venue to be blank.
   * @param rawTitle - the tile of the paper
   * @param rawAuthors - the list of authors
   * @param date - the date of publication
   * @return - a RawCitation with no venue value
   */
  def apply(rawTitle:String, rawAuthors:List[String], date:String): RawCitation = RawCitation(rawTitle,rawAuthors,date,"")

  /**
   * Creates a RawCitation object from a PaperMetadata object.
   * @param paperMetadata - the PaperMetadata object
   * @return - the corresponding RawCitation
   */
  def fromPaperMetadata(paperMetadata: PaperMetadata) = RawCitation(paperMetadata.title,paperMetadata.authors,paperMetadata.year.toString,paperMetadata.venue)
}
