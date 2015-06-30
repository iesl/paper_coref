package org.allenai.scholar.paper_coref.data_structures

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

/**
 * Trait which specifies methods for serializing data structures to JSON 
 */
trait JSONSerializable {
  implicit val formats = Serialization.formats(NoTypeHints)

  /**
   * A compact 1-line JSON string  
   * @return serialized string
   */
  def toJSON: String = write(this)

  /**
   * A multi-line JSON string
   * @return serialized string
   */
  def toJSONFormatted: String = writePretty(this)
}


