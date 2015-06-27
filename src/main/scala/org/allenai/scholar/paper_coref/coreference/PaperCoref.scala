package org.allenai.scholar.paper_coref.coreference

import org.allenai.scholar.paper_coref.data_structures.PaperMention

/**
 * The base trait of paper coreference algorithms. The paper coreference algorithm takes as input a collection of paper 
 * "mentions," which are ambiguous references to a research paper. These mentions are extracted headers and reference
 * citations from papers. The goal of paper coreference is to group the mention by the paper to which they refer.
 */
trait PaperCoref {

  /**
   * The name of the coreference algorithm 
   */
  val name: String

  /**
   * Run the coreference algorithm on the given set of mentions. 
   * @param mentions - the inputted mentions
   * @return - the clustering of the mentions
   */
  def performCoref(mentions:Iterable[PaperMention]):Iterable[Iterable[PaperMention]]
}

/**
 * Convenience methods for the paper coreference algorithms.  
 */
object PaperCoref {

  // Listing of all known coref algorithms
  private val allPaperCorefAlgorithms = Iterable(Baseline,AlphaOnly).map((alg) => alg.name -> alg).toMap

  /**
   * Given the name of a coref algorithm, return an instance of that algorithm* 
   * @param string - the name of the algorithm
   * @return - the algorithm with the given name.
   */
  def apply(string: String) = allPaperCorefAlgorithms(string)

}

/**
 * A simple type of coref algorithm which performs a relaxed string match on the title of the papers. 
 * The way in which the match is relaxed is defined by the titleHash function. Coreference groups papers 
 * with the same titleHash together. Papers without titles are left as singletons.
 */
trait HashingCoref extends PaperCoref {

  val name = "HashingCoref"

  def titleHash(title:String):String

  def performCoref(mentions: Iterable[PaperMention]) = mentions.groupBy(m => if(m.title.nonEmpty) titleHash(m.title) else m.id).values
}