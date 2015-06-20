package org.allenai.scholar.paper_coref.data_structures

case class Date(rawString: String) {

  val year = """\d{4}""".r.findFirstIn(rawString).getOrElse("") // TODO: Keep this as an option?
}
