package org.allenai.scholar.paper_coref.data_structures

import org.apache.commons.lang3.StringUtils._


/**
 * Following the definition of the Authors in meta-eval
 */
case class Author(first: String, middles: Seq[String], last: String) {
  
  def normalized: Author = Author(AuthorName.normalize(first),middles.map(AuthorName.normalize),AuthorName.normalize(last))
  
  def formattedString = last.mkString("","",", ") + (first +: middles).mkString(" ")

}

object AuthorName {
  
  def normalize(nameString: String) = stripAccents(nameString.toLowerCase.trim).replaceAll("""\s+""", " ")
  
}