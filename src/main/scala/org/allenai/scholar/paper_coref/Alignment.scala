package org.allenai.scholar.paper_coref

import scala.collection.mutable
import org.allenai.scholar.paper_coref.citations.LocatedCitation
import java.io.File
import cc.factorie.app.strings

case class Alignable[T](alignOn:String, content:T)
case class AlignResult[A,B](aligned:Iterable[(A,B)], unalignedSources:Iterable[A], unalignedTargets:Iterable[B])
object Alignment {

  val scoreDistPos = {(s1:String, s2:String) =>
    /*
    val rawScore = strings.editDistance(s1, s2)
    if(rawScore > 10) {
      0
    } else {
      Seq(s1.size - s2.size).max - rawScore
    }.toDouble
    */
    val rawScore = Seq(s1.size, s2.size).max - strings.editDistance(s1, s2)
    (if(rawScore > 10) rawScore else 0).toDouble
  }

  // this should be the Hungarian algorithm, produces the weighted bipartite matching with the highest score
  // this is probably overkill though, so for now we pick the min weight for each alignable and log and throw when there is a conflict
  def align[A,B](sources:Iterable[Alignable[A]], targets:Iterable[Alignable[B]], scoreEdge:((String, String) => Double)):Seq[(A, B)] = {
    val (sourcesPadded, targetsPadded) = if(sources.size != targets.size) {
      println("Expected the same number of sources and targets but found %d sources and %d targets".format(sources.size, targets.size))
      println("Sources are:\n%s".format(sources))
      println("Taregts are:\n%s".format(targets))
      if(sources.size > targets.size) {
        // todo null!!!!!!!
        sources -> (targets ++ Iterable.fill(sources.size - targets.size)(Alignable("", null.asInstanceOf[B])))
      } else {
        (sources ++ Iterable.fill(targets.size - sources.size)(Alignable("", null.asInstanceOf[A]))) -> targets
      }
    } else {
      sources -> targets
    }

    val alignment = sourcesPadded.filter{case Alignable(s, c) => c != null}.map {case Alignable(sourceAlign, sourceContent) =>
      val Alignable(_, targetContent) = targetsPadded.maxBy{case Alignable(targetAlign, _) => scoreEdge(targetAlign, sourceAlign)}
      if(targetContent == null) {
        println("WARNING: aligned %s to empty string (score %.3f) in preference to:\n%s".format(sourceAlign, scoreEdge("", sourceAlign), targetsPadded.map{case Alignable(tA, cont) => scoreEdge(tA, sourceAlign) + "\t" + cont}.mkString("\n")))
      }
      sourceContent -> targetContent
    }


    var sourceToTargetViolations = 0
    var targetToSourceViolations = 0
    alignment.groupBy(_._1).foreach { case(source, ts) =>
      if(ts.size > 1) {
        println("Expected a single target to align to each source but found %d".format(ts.size))
        //println("Aligned all of %s to %s".format(ts.mkString("\t"), source))
        sourceToTargetViolations += 1
      }
    }
    alignment.groupBy(_._2).foreach{ case (target, ss) =>
      if(ss.size > 1) {
        println("Expected a single source to align to each target but found %d".format(ss.size))
        targetToSourceViolations += 1
      }
    }

    println("Out of %d raw sources and %d raw targets, there were %d source to target violations (%.4f percent) and %d target to source violations (%.4f percent)"
      .format(sources.size, targets.size, sourceToTargetViolations, sourceToTargetViolations.toDouble/sources.size, targetToSourceViolations, targetToSourceViolations.toDouble/targets.size))

    alignment.toSeq
  }

  // assumes that scoreEdge >=0, and is 0 if the two are unlinkable
  def jaggedAlign[A,B](sources:Iterable[Alignable[A]], targets:Iterable[Alignable[B]], scoreEdge:(String, String) => Double):AlignResult[A,B] = {
    val edges = for (Alignable(src, srcCon) <- sources;
                     Alignable(tgt, tgtCon) <- targets;
                     weight = scoreEdge(src, tgt)
                     if weight > 0.0 && src.nonEmpty && tgt.nonEmpty) yield {
      (weight, srcCon, tgtCon)
    }

    //println("with %d sources and %d targets I generated %d total edges out of a possible %d".format(sources.size, targets.size, edges.size, sources.size * targets.size))

    //println(edges mkString "\n")

    val remainingTargets = mutable.HashSet().++=(edges.map(_._3))
    val remainingSources = mutable.HashSet().++=(edges.map(_._2))
    val linkedEdges = mutable.ArrayBuffer[(A,B)]()

    //println("Before starting I have %d targets and %d sources to align".format(remainingTargets.size, remainingSources.size))
    edges.toSeq.sortBy{case (weight,_,_) => - weight}.foreach { case (w, src, tgt) =>
      //println("Looking at an edge of weight %.4f between src:%s and target:%s".format(w,src,tgt))
      if(remainingSources.contains(src) && remainingTargets.contains(tgt)) { // if we haven't assigned either of these
        //println("I decided to link src:%s and tgt:%s".format(src, tgt))
        linkedEdges += src -> tgt
        remainingSources -= src
        remainingTargets -= tgt
      }
    }
    AlignResult(linkedEdges, remainingSources, remainingTargets)
  }

  def main(args:Array[String]) {

    val citsDir = args(0)
    val locCits = new File(citsDir).listFiles().flatMap(f => LocatedCitation.fromFile(f.getAbsolutePath)).toIterable
    val citsByPaper = locCits.groupBy(_.foundInId)

    val goldCitDocs = {
      val pms = PaperMetadata.fromFile(args(1))
      val bcs = BareCitation.fromFile(args(2))
      val pmMap = pms.map(p => p.id -> p).toMap
      bcs.groupBy(_.from).flatMap{ case (k, vs) =>
        pmMap.get(k).map(pm => GoldCitationDoc(pm, vs.flatMap(v => pmMap.get(v.to))))
      }.map(g => g.doc.id -> g).toMap
    }
    val alignedPapers = citsByPaper.keySet.intersect(goldCitDocs.keySet).map(k => citations.ParsedPaper.fromCitations(citsByPaper(k)) -> goldCitDocs(k)).toIterable
    val scoreDistPos = {(s1:String, s2:String) => (Seq(s1.size, s2.size).max - strings.editDistance(s1, s2)).toDouble}

    val (pred, gold) = alignedPapers.head

    println("Aligning found citations:\n" + pred.bib.map(_.rawCitation.rawTitle))
    println("With Gold Citations:\n" + gold.citations.map(_.title))

    val sources = pred.bib.map(p => Alignable(p.rawCitation.rawTitle, p))
    val targets = gold.citations.map(p => Alignable(p.title, p))
    val res = Alignment.jaggedAlign(sources, targets, scoreDistPos)

    println(res)
  }
}



