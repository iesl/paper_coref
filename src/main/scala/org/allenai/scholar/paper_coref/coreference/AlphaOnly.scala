package org.allenai.scholar.paper_coref.coreference

/**
 * A very simple coreference scheme which considers two papers coreferent if the titles are a case insensitive match. 
 */
object AlphaOnly extends HashingCoref {

  override val name = "AlphaOnly"

  /**
   * This implementation of titleHash lowercases the title and normalizes white spaces. *
   * @param title - the title of the paper as it appeared in the extraction
   * @return - the relaxed version of the title.
   */
  def titleHash(title: String) = title.replaceAll("""\s+""", " ").toLowerCase.replaceAll("""^[\w]""","")
}
