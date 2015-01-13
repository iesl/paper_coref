package org.allenai.scholar.paper_coref

import cc.factorie.app.strings
import java.io.{BufferedWriter, FileWriter}

case class GoldCitationDoc(doc:PaperMetadata, citations:Iterable[PaperMetadata])
/**
 * @author John Sullivan
 */
object BuildGoldAnnotations {
  def scoreEdge(reference:String, observed:String):Double = -strings.editDistance(reference, observed).toDouble

  def labelCitations(rawDocs:Iterable[ParsedPaper], paperMetadata:Iterable[PaperMetadata], bareCitations:Iterable[BareCitation]):Iterable[Citation] = {
    val metadataMap = paperMetadata.map(m => m.id -> m).toMap

    val goldCitDocs = bareCitations.groupBy(_.from).map{ case (k, vs) =>
      GoldCitationDoc(metadataMap(k), vs.map(v => metadataMap(v.to)))
    }

    val predPapers = rawDocs.map{ pd => Alignable(pd.selfCit.rawTitle, pd) }
    val goldPapers = goldCitDocs.map{ gcd => Alignable(gcd.doc.title, gcd)}

    val paperAlignment = Alignment.align(predPapers, goldPapers, scoreEdge)

    paperAlignment.flatMap { case (parsedDoc, goldDoc) =>
      val predBib = parsedDoc.bib.map(rc => Alignable(rc.rawTitle, rc))
      val goldBib = goldDoc.citations.map(gc => Alignable(gc.title, gc))

      Alignment.align(predBib, goldBib, scoreEdge).map { case (predCit, paperMeta) =>
        predCit withGoldLabel paperMeta.id
      }.+:(parsedDoc.selfCit withGoldLabel goldDoc.doc.id)
    }
  }

  def main(args:Array[String]) {
    val pm = PaperMetadata fromFile args(0)
    println("Loaded metadata")
    val cg = BareCitation fromFile args(1)
    println("Loaded citation graph")
    val cits = ParsedPaper loadFromDir args(2)
    println("Loaded citations")
    val wrt = new BufferedWriter(new FileWriter(args(3)))

    val labelled = labelCitations(cits, pm, cg)

    println ("Labelled citations")

    labelled.zipWithIndex foreach { case(cit, idx) =>
      wrt write  cit.toJsonString
      wrt.newLine()
      if(idx % 1000 == 0) {
        println("Wrote %d lines".format(idx))
        wrt.flush()
      }
    }
    wrt.flush()
    wrt.close()
    println("Writing complete")
  }

  /*
  def labelCitationsX = {

    val metadataMap = paperMetadata.get.map(m => m.id -> m).toMap

    val goldCitDocs = bareCitations.get.groupBy(_.from).map{ case (k, vs) =>
      GoldCitationDoc(metadataMap(k), vs.map(v => metadataMap(v.to)))
    }

    val predPapers = parscitDocs.get.map{ pd => Alignable(pd.doc.title, pd) }
    val goldPapers = goldCitDocs.map{ gcd => Alignable(gcd.doc.title, gcd)}

    val paperAlignment = Alignment.align(predPapers.collect(), goldPapers, scoreEdge)

    sparkContext.parallelize(paperAlignment.flatMap { case (parsedDoc, goldDoc) =>
      val predBib = parsedDoc.doc.rawCitations.map(rc => Alignable(rc.rawTitle, rc))
      val goldBib = goldDoc.citations.map(gc => Alignable(gc.title, gc))

      Alignment.align(predBib, goldBib, scoreEdge).map { case (rc, pm) =>
        LabeledCitation(pm.id, rc)
      }.+:(LabeledCitation(goldDoc.doc.id, RawCitation(parsedDoc.doc.title, parsedDoc.doc.authors, "")))
    })
  }
  */
}

case class Alignable[T](alignOn:String, content:T)
object Alignment {

  // this should be the Hungarian algorithm, produces the weighted bipartite matching with the highest score
  // this is probably overkill though, so for now we pick the min weight for each alignable and log and throw when there is a conflict
  def align[A,B](sources:Iterable[Alignable[A]], targets:Iterable[Alignable[B]], scoreEdge:((String, String) => Double)):Seq[(A, B)] = {
    val (sourcesPadded, targetsPadded) = if(sources.size != targets.size) {
      println("Expected the same number of sources and targets but found %d sources and %d targets".format(sources.size, targets.size))
      if(sources.size > targets.size) {
        // todo null!!!!!!!
        sources -> (targets ++ Iterable.fill(sources.size - targets.size)(Alignable("", null.asInstanceOf[B])))
      } else {
        (sources ++ Iterable.fill(targets.size - sources.size)(Alignable("", null.asInstanceOf[A]))) -> targets
      }
    } else {
      sources -> targets
    }

    val alignment = sourcesPadded.map {case Alignable(sourceAlign, sourceContent) =>
      val Alignable(_, targetContent) = targetsPadded.maxBy{case Alignable(targetAlign, _) => scoreEdge(targetAlign, sourceAlign)}
      sourceContent -> targetContent
    }.toSeq


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

    alignment
  }
}


