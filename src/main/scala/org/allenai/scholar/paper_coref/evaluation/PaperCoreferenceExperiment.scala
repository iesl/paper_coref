package org.allenai.scholar.paper_coref.evaluation

import java.io._
import java.util

import cc.factorie.util.EvaluatableClustering
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.coreference.PaperCoref
import org.allenai.scholar.paper_coref.data_structures._
import org.allenai.scholar.paper_coref.load._

import scala.collection.JavaConverters._


/**
 * Paper coreference experiment class. Used to perform and evaluate coreference on a set of PaperMentions
 * Each of the given coref algorithms is evaluated.  
 * @param mentions - the mentions to cluster
 * @param corefs - the coref algorithms to use
 */
class PaperCoreferenceExperiment(val mentions: Iterable[PaperMention], val corefs: Iterable[PaperCoref]) {

  private val _clusteringResults = new util.HashMap[String, Iterable[Iterable[PaperMention]]]().asScala
  
  lazy val goldClustering = mentions.groupBy(_.trueLabel).map(_._2)

  /**
   * Returns all of the coreference results.  
   * @return - A map from PaperCoref algorithm names to the coreference results
   */
  def predictedClusteringResults = _clusteringResults

  /**
   * The coreference results of the algorithm with the given name. 
   * @param corefAlgName - the name of the algorithm
   * @return - the coref results for corefAlgName
   */
  def predictedClustering(corefAlgName: String) = _clusteringResults.get(corefAlgName)

  /**
   * Executes each of the coreference algorithms on the set of mentions.
   * @return - Pairs of coreference algorithm names and results 
   */
  def run() = {
    println("[PaperCoreferenceExperiment] Running Experiment")
    corefs map { c =>
      val start = System.currentTimeMillis()
      println(s"[PaperCoreferenceExperiment] Running Coref: ${c.name}")
      _clusteringResults.put(c.name,c.performCoref(mentions))
      println(s"[PaperCoreferenceExperiment] Finished performing coreference.")
      println("[PaperCoreferenceExperiment] Creating gold clustering.")
      val gold = _clusteringResults(c.name).trueClustering
      println("[PaperCoreferenceExperiment] Done creating gold clustering.")
      println("[PaperCoreferenceExperiment] Creating predicted clustering.")
      val pred = _clusteringResults(c.name).predictedClustering
      println("[PaperCoreferenceExperiment] Done creating predicted clustering.")
      val totalTime = System.currentTimeMillis() - start
      println(s"[PaperCoreferenceExperiment] Time to run ${c.name}: $totalTime ms")
      println(s"[PaperCoreferenceExperiment] Results for: ${c.name}")
      val resultString = EvaluatableClustering.evaluationString(pred, gold)
      println(resultString)
      (c.name, resultString)
    }
  }

  /**
   * Alternate constructor. Constructs PaperMentions from the extracted ParsedPaper and gold data using the
   * PaperMention.generate function.  
   * @param citationMap - Mapping from Paper Id to ParsedPaper object
   * @param goldCitationDocumentMap - Mapping from Paper Id to GoldCitationDoc object
   * @param corefs - the coref algorithms to use
   * @return
   */
  def this(citationMap: Map[String,ParsedPaper], goldCitationDocumentMap: Map[String, GoldCitationDoc], corefs: Iterable[PaperCoref]) = 
    this(PaperMention.generate(citationMap, goldCitationDocumentMap),corefs)

  /**
   * Alternate constructor. Constructs the citationMap and goldCitationDocumentMap used in other constructor 
   * from the extractions and the gold meta data and citation edges.  
   * @param parsedPapers - extracted papers 
   * @param goldPaperMetaData - gold paper metadata 
   * @param goldCitationEdges - gold citation edges
   * @param corefs - coref algorithms to use
   * @return
   */
  def this(parsedPapers: Iterable[ParsedPaper], goldPaperMetaData: Iterable[PaperMetadataWithId], goldCitationEdges: Iterable[BareCitation], corefs: Iterable[PaperCoref]) =
    this(parsedPapers.groupBy(_.self.foundInId).mapValues(_.head),
    {
      val pmMap = goldPaperMetaData.map(p => p.id -> p).toMap
      // take each paper's group of references and create a pair (this paper's metadata, meta data of this paper's citations)
      // create map from id to this info
      goldCitationEdges.groupBy(_.from).flatMap{ case (k, vs) =>
        pmMap.get(k).map(pm => GoldCitationDoc(pm, vs.flatMap(v => pmMap.get(v.to))))
      }.map(g => g.doc.id -> g).toMap
    },
    corefs)


  /**
   * Alternate constructor. Takes as input an iterable of ParsedPapers and the gold data filenames.
   * @param parsedPapers - ParsedPaper extractions
   * @param codec - the encoding of the gold data files
   * @param goldMetaDataFilename - the meta data file
   * @param goldCitationsFilename - the citation edges file
   * @param corefs - the coref algorithms
   */
  def this(parsedPapers: Iterable[ParsedPaper], codec: String, goldMetaDataFilename: String, goldCitationsFilename: String, corefs: Iterable[PaperCoref]) = {
    this(parsedPapers,PaperMetadataWithId.fromJSONFile(goldMetaDataFilename),BareCitation.fromFile(goldCitationsFilename),corefs)
  }

    /**
   * Loads data from given arguments and uses other constructor to create experiment.
   * @param loader - the loader to use
   * @param citationFiles - the files to load ParsedPapers from
   * @param codec - the encoding of the files
   * @param goldMetaDataFilename - the filename of the gold metadata file
   * @param goldCitationsFilename - the filename of the gold citations file
   * @param corefs - the coref algorithms to use.
   */
  def this(loader: Loader, citationFiles: Iterable[File], codec: String, goldMetaDataFilename: String, goldCitationsFilename: String, corefs: Iterable[PaperCoref]) = {
    this(loader.fromFiles(citationFiles,codec),PaperMetadataWithId.fromJSONFile(goldMetaDataFilename),BareCitation.fromFile(goldCitationsFilename),corefs)
  }
}