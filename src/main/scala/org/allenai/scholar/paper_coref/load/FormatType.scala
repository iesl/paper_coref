package org.allenai.scholar.paper_coref.load


/**
 * FormatType specifies the input data format. 
 */
sealed trait FormatType {

  /**
   * The string representation of the format.  
   */
  val name: String
}

/**
 * The ResearchPaperProcessor (RPP) input format.  
 */
case object RPPFormat extends FormatType {
  val name = "RPP"
}

/**
 * The Grobid input format
 */
case object GrobidFormat extends FormatType {
  val name = "Grobid"
}

/**
 * The ParsCit input format  
 */
case object ParsCitFormat extends FormatType {
  val name = "ParsCit"  
}

/**
 * JSON formatted LocatedCitation data using compact (1-line per object) JSON format. 
 */
case object LocatedCitationFormat extends FormatType {
  val name = "LocatedCitation"
}

/**
 * JSON formatted ParsedPaper data using compact (1-line per object) JSON format. 
 * TODO: I think this is not supported?  
 */
case object ParsedPaperFormat extends FormatType {
  val name = "ParsedPaper"
}

/**
 * JSON formatted PaperMetaData data using compact (1-line per object) JSON format. 
 */
case object PaperMetadataFormat extends FormatType {
  val name = "PaperMetadata"
}

/**
 * The CORA dataset format
 */
case object CoraFormat extends FormatType {
  val name = "Cora"
}

/**
 * Helper methods for format type. 
 */
object FormatType {

  // Listing of all known format types
  private val allFormatTypes = Iterable(RPPFormat,GrobidFormat,ParsCitFormat,LocatedCitationFormat,ParsedPaperFormat,PaperMetadataFormat,CoraFormat).map( (ft) => ft.name -> ft).toMap

  /**
   * Given the string version of a format type, produce the associated object
   * @param string - name of format type
   * @return
   */
  def apply(string: String) = allFormatTypes(string)
  
}