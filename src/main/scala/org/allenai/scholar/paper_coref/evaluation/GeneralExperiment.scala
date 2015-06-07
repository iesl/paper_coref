package org.allenai.scholar.paper_coref.evaluation

import java.io.File

import cc.factorie.util.EvaluatableClustering
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.load.{LoadParsCit, XMLLoader}

// TODO: Make this interface nicer
class GeneralExperiment(loader: XMLLoader, citationFiles: Iterable[File], goldMetaData: String, goldCitations: String) extends ExperimentRunner {

  println(s"[GeneralExperiment] Loading citations from ${citationFiles.size} files.")
  val parsedPapers = loader.fromFiles(citationFiles)
  println(s"[GeneralExperiment] Done Loading citations.")
  
  println(s"[GeneralExperiment] Creating citation map")
  val citsMap = parsedPapers.groupBy(_.self.foundInId).mapValues(_.head)
  println(s"[GeneralExperiment] Done creating citation map")
  
  println("[GeneralExperiment] Loading gold citations")
  val goldCitDocs = {
    println("[GeneralExperiment] Loading Paper Metadata")
    val pms = PaperMetadata.fromFile(goldMetaData)
    println("[GeneralExperiment] Done loading paper metadata")
    println("[GeneralExperiment] Loading gold citations")
    val bcs = BareCitation.fromFile(goldCitations)
    println("[GeneralExperiment] Done loading gold citations")
    println("[GeneralExperiment] Creating pmMap")
    val pmMap = pms.map(p => p.id -> p).toMap
    println("[GeneralExperiment] Done creating pmMap")
    println("[GeneralExperiment] Finalizing gold map.")
    bcs.groupBy(_.from).flatMap{ case (k, vs) =>
      pmMap.get(k).map(pm => GoldCitationDoc(pm, vs.flatMap(v => pmMap.get(v.to))))
    }.map(g => g.doc.id -> g).toMap
  }
  println(s"[GeneralExperiment] done loading gold citations")


  lazy val mentions = {
    println("[GeneralExperiment] Creating paper mentions.")
    val res = PaperMention.generate(citsMap, goldCitDocs)
    println("[GeneralExperiment] Done creating paper mentions.")
    res
  }
  lazy val corefs = Seq(Baseline, AlphaOnly)
  override def run() {
    println("[GeneralExperiment] Running Experiment")
    corefs foreach { c =>
      println(s"[GeneralExperiment] Running Coref: ${c.getClass.getName.toString}")
      val res = c.performCoref(mentions)
      println(s"[GeneralExperiment] Finished performing coreference.")
      println("[GeneralExperiment] Creating gold clustering.")
      val gold = res.trueClustering
      println("[GeneralExperiment] Done creating gold clustering.")
      println("[GeneralExperiment] Creating predicted clustering.")
      val pred = res.predictedClustering
      println("[GeneralExperiment] Done creating predicted clustering.")
      println("[GeneralExperiment] Results for: " + c.getClass.getName)
      println(EvaluatableClustering.evaluationString(pred, gold))
    }
  }
}

object ParscitTest {
  
  def main(args: Array[String]): Unit = {
    val parscitFiles = new File("/iesl/canvas/nmonath/grant_work/ai2/data/parscit-acl-processed/extract_all").listFiles().filter(_.getName.endsWith(".xml"))
    val goldMetaData = "metadata"
    val goldCitations = "citation-edges"
    val exp = new GeneralExperiment(LoadParsCit,parscitFiles,goldMetaData,goldCitations)
    exp.run()
  }
  
}
