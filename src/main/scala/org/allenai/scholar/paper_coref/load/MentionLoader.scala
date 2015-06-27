package org.allenai.scholar.paper_coref.load

import java.io.File

import org.allenai.scholar.paper_coref.data_structures.PaperMention


trait MentionLoader extends Loader {

  def loadMentionsFromFile(file: File, codec: String) : Iterable[PaperMention]
  
}
