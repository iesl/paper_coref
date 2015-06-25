package org.allenai.scholar.paper_coref.load

sealed trait FormatType {

  val name: String
}

case object RPPFormat extends FormatType {
  val name = "RPP"
}

case object GrobidFormat extends FormatType {
  val name = "Grobid"
}

case object ParsCitFormat extends FormatType {
  val name = "ParsCit"  
}

case object LocatedCitationFormat extends FormatType {
  val name = "LocatedCitation"
}

case object PaperMetaDataFormat extends FormatType {
  val name = "PaperMetaData"
}

case object CoraFormat extends FormatType {
  val name = "Cora"
}

object FormatType {

  private val allFormatTypes = Iterable(RPPFormat,GrobidFormat,ParsCitFormat,LocatedCitationFormat,CoraFormat).map( (ft) => ft.name -> ft).toMap
  
  def apply(string: String) = allFormatTypes(string)
  
}