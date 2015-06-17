package org.allenai.scholar.paper_coref.evaluation

import java.io.File

import cc.factorie.util.EvaluatableClustering
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.load._

import scala.io.Source

// TODO: Make this interface nicer
class GeneralExperiment(loader: Loader, citationFiles: Iterable[File], goldMetaData: String, goldCitations: String) extends ExperimentRunner {

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
    // pairs of (from -> to) paper citations (a -> b) a cites b
    val bcs = BareCitation.fromFile(goldCitations)
    println("[GeneralExperiment] Done loading gold citations")
    println("[GeneralExperiment] Creating pmMap")
    // Map from paper id to meta data
    val pmMap = pms.map(p => p.id -> p).toMap
    println("[GeneralExperiment] Done creating pmMap")
    println("[GeneralExperiment] Finalizing gold map.")
    
    // take each paper's group of references and create a pair (this paper's metadata, meta data of this paper's citations)
    // create map from id to this info
    
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
      println(s"[GeneralExperiment] Running Coref: ${c.getClass.getName}")
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


// TODO: set these up w/o hard coding.
object ParscitTest {
  
  def main(args: Array[String]): Unit = {
    val aclIds = Source.fromFile("data/acl_paper_ids.txt").getLines().toSet[String]
    val parscitFiles = new File("/iesl/canvas/nmonath/grant_work/ai2/data/parscit-acl-processed/extract_all").listFiles().filter((f) => f.getName.endsWith(".xml") && aclIds.contains(f.getNameWithoutExtension))
    val goldMetaData = "metadata"
    val goldCitations = "citation-edges"
    val exp = new GeneralExperiment(LoadParsCit,parscitFiles,goldMetaData,goldCitations)
    exp.run()
  }
  
}


object GrobidTest {

  def main(args: Array[String]): Unit = {
    val aclIds = Source.fromFile("data/acl_paper_ids.txt").getLines().toSet[String]
    val grobidFiles = new File("/iesl/canvas/nmonath/grant_work/ai2/data/grobid-acl-processed/full-text").listFiles().filter( (f) => f.getName.endsWith(".xml") && aclIds.contains(f.getNameWithoutExtension))
    val goldMetaData = "metadata"
    val goldCitations = "citation-edges"
    val exp = new GeneralExperiment(LoadGrobid,grobidFiles,goldMetaData,goldCitations)
    exp.run()
  }

}

object RPPTest {

  def main(args: Array[String]): Unit = {
    val aclIds = Source.fromFile("data/acl_paper_ids.txt").getLines().toSet[String]
    val rppFiles = new File("/iesl/canvas/nmonath/grant_work/ai2/data//rpp-processed-output/").listFiles().filter( (f) => f.getName.endsWith(".tagged") && aclIds.contains(f.getNameWithoutExtension))
    val goldMetaData = "data/metadata"
    val goldCitations = "data/citation-edges"
    val exp = new GeneralExperiment(LoadRPP,rppFiles,goldMetaData,goldCitations)
    exp.run()
  }

}