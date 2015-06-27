package org.allenai.scholar.paper_coref.data_structures

/**
 * Following the representation used in Meta-Eval, this 
 * represents a date in the coreference system.  
 * @param rawString - date string
 */
case class Date(rawString: String) {

  /**
   * The year given in the date string.
   */
  val year = """\d{4}""".r.findFirstIn(rawString).getOrElse("") // TODO: Keep this as an option?
}
