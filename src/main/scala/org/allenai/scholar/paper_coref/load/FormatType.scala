package org.allenai.scholar.paper_coref.load

sealed trait FormatType {

  val name: String
}

case object RPP extends FormatType {
  val name = "RPP"
}

case object Grobid extends FormatType {
  val name = "Grobid"
}

case object ParsCit extends FormatType {
  val name = "ParsCit"  
}

object FormatType {

  private val allFormatTypes = Iterable(RPP,Grobid,ParsCit).map( (ft) => ft.name -> ft).toMap
  
  def apply(string: String) = allFormatTypes(string)
  
}