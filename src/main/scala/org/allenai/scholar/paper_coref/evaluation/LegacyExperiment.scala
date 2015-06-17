package org.allenai.scholar.paper_coref.evaluation

import java.io.File

import cc.factorie.util.EvaluatableClustering
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.coreference.{AlphaOnly, Baseline, PaperCoref}
import org.allenai.scholar.paper_coref.data_structures._

trait ExperimentRunner {
  def corefs:Iterable[PaperCoref]
  def mentions:Iterable[PaperMention]

  def run() {
    corefs foreach { c =>
      val res = c.performCoref(mentions)
      val gold = res.trueClustering
      val pred = res.predictedClustering
      println("Results for: " + c.getClass.getName)
      println(EvaluatableClustering.evaluationString(pred, gold))
    }
  }
}

object Experiments extends App with ExperimentRunner {
  val citsDir = args(0)
  val locCits = new File(citsDir).listFiles().flatMap(f => LocatedCitation.fromFile(f.getAbsolutePath)).toIterable

  val goldCitDocs = {
    val pms = PaperMetadata.fromFile(args(1))
    val bcs = BareCitation.fromFile(args(2))
    val pmMap = pms.map(p => p.id -> p).toMap
    bcs.groupBy(_.from).flatMap{ case (k, vs) =>
      pmMap.get(k).map(pm => GoldCitationDoc(pm, vs.flatMap(v => pmMap.get(v.to))))
    }.map(g => g.doc.id -> g).toMap
  }
  val citsMap = locCits.groupBy(_.foundInId).mapValues(ParsedPaper.fromCitations)


  val mentions = PaperMention.generate(citsMap, goldCitDocs)
  val corefs = Seq(Baseline, AlphaOnly)


  run()
}
