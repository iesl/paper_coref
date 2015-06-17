package org.allenai.scholar.paper_coref.coreference

import org.allenai.scholar.paper_coref.data_structures.PaperMention

trait PaperCoref {

  val name: String

  def performCoref(mentions:Iterable[PaperMention]):Iterable[Iterable[PaperMention]]
}

object PaperCoref {

  private val allPaperCorefAlgorithms = Iterable(Baseline,AlphaOnly).map((alg) => alg.name -> alg).toMap

  def apply(string: String) = allPaperCorefAlgorithms(string)

}

trait HashingCoref extends PaperCoref {

  val name = "HashingCoref"

  def titleHash(title:String):String

  def performCoref(mentions: Iterable[PaperMention]) = mentions.groupBy(m => if(m.title.nonEmpty) titleHash(m.title) else m.id).values
}