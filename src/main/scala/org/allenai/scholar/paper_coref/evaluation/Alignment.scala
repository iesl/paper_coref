package org.allenai.scholar.paper_coref.evaluation

import cc.factorie.app.strings

import scala.collection.mutable

case class Alignable[T](alignOn:String, content:T)
case class AlignResult[A,B](aligned:Iterable[(A,B)], unalignedSources:Iterable[A], unalignedTargets:Iterable[B])
object Alignment {

  val scoreDistPos = {(s1:String, s2:String) =>
    val rawScore = Seq(s1.size, s2.size).max - strings.editDistance(s1, s2)
    (if(rawScore > 10) rawScore else 0).toDouble
  }

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



