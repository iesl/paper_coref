package org.allenai.scholar.paper_coref.load

import java.io.File

import org.allenai.scholar.paper_coref.data_structures.PaperMention

/**
 * A trait for loaders which can load data in the format of PaperMentions rather than the standard
 * ParsedPaper data structures 
 */
trait MentionLoader extends Loader {

  /**
   * Load the mentions from the given file. *
   * @param file - the file to load the mentions from
   * @param codec - the encoding of the file
   * @return - all of the mentions appearing in the file.
   */
  def loadMentionsFromFile(file: File, codec: String) : Iterable[PaperMention]
  
}
