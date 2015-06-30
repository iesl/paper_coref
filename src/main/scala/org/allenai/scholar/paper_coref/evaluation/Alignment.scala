package org.allenai.scholar.paper_coref.evaluation

import cc.factorie.app.strings

import scala.collection.mutable

/**
 * Auxiliary data structure to align predicted mentions and gold mentions. 
 * @param alignOn - string to align data on
 * @param content - the content to be aligned
 * @tparam T - the type of the content
 */
case class Alignable[T](alignOn:String, content:T)

/**
 * Auxiliary data structure storing the result of aligning Alignable items.
 * @param aligned - the pairs of aligned data
 * @param unalignedSources - the unaligned data items of type A
 * @param unalignedTargets - the unaligned data items of type B
 * @tparam A - the first type of item
 * @tparam B - the second type of item
 */
case class AlignResult[A,B](aligned:Iterable[(A,B)], unalignedSources:Iterable[A], unalignedTargets:Iterable[B])

/**
 * Used to align predicted & gold mentions 
 */
object Alignment {

  val scoreDistPos = {(s1:String, s2:String) =>
    val rawScore = Seq(s1.size, s2.size).max - strings.editDistance(s1, s2)
    (if(rawScore > 10) rawScore else 0).toDouble
  }

  /**
   * Aligns a set of sources and targets using scoring function on the  alignOn field (e.g. edit distance).
   * @param sources - one group of data
   * @param targets - other group of data
   * @param scoreEdge - scoring function
   * @tparam A - type of sources
   * @tparam B - type of targets
   * @return - the result of the alignment.
   */
  // assumes that scoreEdge >=0, and is 0 if the two are unlinkable
  def jaggedAlign[A,B](sources:Iterable[Alignable[A]], targets:Iterable[Alignable[B]], scoreEdge:(String, String) => Double):AlignResult[A,B] = {
    val edges = for (Alignable(src, srcCon) <- sources;
                     Alignable(tgt, tgtCon) <- targets;
                     weight = scoreEdge(src, tgt)
                     if weight > 0.0 && src.nonEmpty && tgt.nonEmpty) yield {
      (weight, srcCon, tgtCon)
    }

    val remainingTargets = mutable.HashSet().++=(edges.map(_._3))
    val remainingSources = mutable.HashSet().++=(edges.map(_._2))
    val linkedEdges = mutable.ArrayBuffer[(A,B)]()

    edges.toSeq.sortBy{case (weight,_,_) => - weight}.foreach { case (w, src, tgt) =>
      if(remainingSources.contains(src) && remainingTargets.contains(tgt)) { // if we haven't assigned either of these
        linkedEdges += src -> tgt
        remainingSources -= src
        remainingTargets -= tgt
      }
    }
    AlignResult(linkedEdges, remainingSources, remainingTargets)
  }
}



