package org.allenai.scholar.paper_coref.evaluation

import java.io._
import java.util

import cc.factorie._
import cc.factorie.util.{DefaultCmdOptions, EvaluatableClustering}
import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.coreference.PaperCoref
import org.allenai.scholar.paper_coref.data_structures._
import org.allenai.scholar.paper_coref.load._
import scala.collection.JavaConverters._

class PaperCoreferenceExperiment(val mentions: Iterable[PaperMention], val corefs: Iterable[PaperCoref]) {

  private val _clusteringResults = new util.HashMap[String, Iterable[Iterable[PaperMention]]]().asScala
  
  lazy val goldClustering = mentions.groupBy(_.trueLabel).map(_._2)
  
  def predictedClusteringResults = _clusteringResults
  
  def predictedClustering(corefAlgName: String) = _clusteringResults.get(corefAlgName)
  
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
  
  
  def this(citationMap: Map[String,ParsedPaper], goldCitationDocumentMap: Map[String, GoldCitationDoc], corefs: Iterable[PaperCoref]) = 
    this(PaperMention.generate(citationMap, goldCitationDocumentMap),corefs)
  
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
  
  def this(loader: Loader, citationFiles: Iterable[File], codec: String, goldMetaDataFilename: String, goldCitationsFilename: String, corefs: Iterable[PaperCoref]) = {
    this(loader.fromFiles(citationFiles,codec),PaperMetadataWithId.fromFile(goldMetaDataFilename),BareCitation.fromFile(goldCitationsFilename),corefs)
  }
  
}



class PaperCoreferenceExperimentOpts extends DefaultCmdOptions {
  val formatType = new CmdOption[String]("format-type", "The format of the input, RPP, ParsCit, Grobid.",true)
  val input = new CmdOption[List[String]]("input", "Either a directory of files, a filename of files, or a list of files", true)
  val inputType = new CmdOption[String]("input-type", "Directory, file of filenames, file", true)
  val inputEncoding = new CmdOption[String]("input-encoding", "UTF-8", "CODEC", "The encoding of the input files")
  val output = new CmdOption[String]("output", "A directory in which to write output to (optional)", false)
  val goldPaperMetaData = new CmdOption[String]("gold-paper-meta-data", "The file containing the ground truth paper meta data", true)
  val goldCitationEdges = new CmdOption[String]("gold-citation-edges", "The file containing the gold citation edges", true)
  val corefAlgorithms = new CmdOption[List[String]]("coref-algorithms", "The names of the coref algorithms to use",true)
}

object PaperCoreferenceExperiment {

  def main(args: Array[String]): Unit = {
    val opts = new PaperCoreferenceExperimentOpts
    opts.parse(args)
    
    
    val formatType = FormatType(opts.formatType.value)

    val citationFiles: Iterable[File] =
      opts.input.value.flatMap((f) =>
        if (opts.inputType.value.equalsIgnoreCase("directory"))
          new File(opts.input.value.head).listFiles()
        else if (opts.inputType.value.equalsIgnoreCase("file of filenames")) {
          new BufferedReader(new FileReader(opts.input.value.head)).toIterator.map(new File(_)).toIterable
        } else if (opts.inputType.value.equalsIgnoreCase("file")) {
          Iterable(new File(f))            
        } else 
          throw new Exception(s"Unknown input type: ${opts.inputType.value}. Must be: directory,file of filenames, file")
      )

    val loader = Loader(formatType)
    val corefs = opts.corefAlgorithms.value.map(PaperCoref.apply)    
    val experiment = new PaperCoreferenceExperiment(loader,citationFiles,opts.inputEncoding.value,opts.goldPaperMetaData.value,opts.goldCitationEdges.value,corefs)
    val result = experiment.run()
    if (opts.output.wasInvoked) {
      val outputDir = new File(opts.output.value)
      outputDir.mkdirs()
      val writer1 = new PrintWriter(new File(outputDir, "results.txt"), "UTF-8")
      writer1.println(result.map( (x) => x._1 + ":\n" + x._2 + "\n\n").mkString("--------------------------------------------\n\n"))
      writer1.close()
    }
  }
}
