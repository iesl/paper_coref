package org.allenai.scholar.paper_coref.coreference

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.segment.DeterministicTokenizer
import cc.factorie.app.strings

/**
 * A relaxed string match coreference algorithm. The algorithm considers two papers as coreferent
 * if the lower-cased, stemmed version of the paper titles excluding punctuation are equal. 
 */
object Baseline extends HashingCoref {

  override val name = "Baseline"

  val fieldSep = Character.toString(31.toChar)

  /**
   * This implementation of titleHash removes punctuation, stems and lowercases the title
   * @param rawTitle - the title as extracted from paper
   * @return - the relaxed version of the title string.
   */
  def titleHash(rawTitle:String):String =
    DeterministicTokenizer.process(new Document(rawTitle))
      .tokens.filterNot(_.isPunctuation).map(t => strings.porterStem(t.string.toLowerCase)).mkString(fieldSep)

  
}
