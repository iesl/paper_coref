package org.allenai.scholar.paper_coref

import cc.factorie.app.strings
import cc.factorie.app.nlp.segment.DeterministicTokenizer
import cc.factorie.app.nlp.Document

/**
 * @author John Sullivan
 */

object Baseline {

  val fieldSep = Character.toString(31.toChar)
  def corefTitleHash(rawTitle:String):String =
    DeterministicTokenizer.process(new Document(rawTitle))
      .tokens.filterNot(_.isPunctuation).map(t => strings.porterStem(t.string.toLowerCase)).mkString(fieldSep)

}

