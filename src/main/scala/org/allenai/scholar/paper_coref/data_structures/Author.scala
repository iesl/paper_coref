package org.allenai.scholar.paper_coref.data_structures

import org.apache.commons.lang3.StringUtils._


/**
 * Follows the definition of the Authors in Meta-Eval. The Author class stores the 
 * first name, middle names, and last name of the author.  
 */
case class Author(first: String, middles: Seq[String], last: String) {

  /**
   * A normalized version of the author. Follows the definition in Meta-Eval. 
   * @return
   */
  def normalized: Author = Author(AuthorName.normalize(first),middles.map(AuthorName.normalize),AuthorName.normalize(last))

  /**
   * A formatted version of the author struct. Follows the definition in Meta-Eval 
   * @return
   */
  def formattedString = last.mkString("","",", ") + (first +: middles).mkString(" ")

}

/**
 * Methods for handling author names 
 */
object AuthorName {

  /**
   * Normalizes an author name component. Follows definition in Meta-Eval
   * @param nameString - the name to normalized
   * @return - normalized version
   */
  def normalize(nameString: String) = stripAccents(nameString.toLowerCase.trim).replaceAll("""\s+""", " ")
  
}