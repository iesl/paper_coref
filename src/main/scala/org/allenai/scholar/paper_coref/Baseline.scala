package org.allenai.scholar.paper_coref

import cc.factorie._
import cc.factorie.app.strings
import cc.factorie.app.nlp.segment.DeterministicTokenizer
import org.json4s._
import org.json4s.native.JsonMethods._
import java.io.{File, FileReader, BufferedReader}
import cc.factorie.util.{EvaluatableClustering, BasicEvaluatableClustering}
import cc.factorie.app.nlp.Document

/**
 * @author John Sullivan
 */

object Baseline {

  val fieldSep = Character.toString(31.toChar)
  def corefTitleHash(rawTitle:String):String =
    DeterministicTokenizer.process(new Document(rawTitle))
      .tokens.filterNot(_.isPunctuation).map(t => strings.porterStem(t.string.toLowerCase)).mkString(fieldSep)


  implicit val f = DefaultFormats
  def readCitations(filename:String) = new Iterator[Citation] {
    private val _iter = new BufferedReader(new FileReader(filename)).toIterator.map{ line =>
      parse(line).extract[Citation]
    }

    def next() = _iter.next()

    def hasNext = {
      if(!_iter.hasNext) {
        println("Processed %s".format(filename))
      }
      _iter.hasNext
    }
  }

  def readLabels(filename:String) = {
    val labelMap = new BufferedReader(new FileReader(filename)).toIterator.flatMap { line =>
      line split """\s+""" match {
        case Array(k, v) => Some(k -> v)
        case otw => println("WARNING: expected to split this into 2, instead got: %s" format otw.toSeq); None
      }
    }.toMap
    labelMap ++ labelMap.values.map(v => v -> v).toMap
  }

  def main(args:Array[String]) {
    val dataFileDir = args(0)
    val labelFile = args(1)

    val goldLabels = readLabels(labelFile)
    val allCitations = new File(dataFileDir).listFiles().toIterator.flatMap(f =>readCitations(f.getAbsolutePath)).toArray

    println("Gold list has %d mentions across %d entities".format(goldLabels.keySet.size, goldLabels.values.toSet.size))

    val citations = allCitations.filter(c => goldLabels.contains(c.goldLabel.get))

    println("Dropped %d of %d citations due to not being on gold list".format(allCitations.size - citations.size, allCitations.size))

    val citCounts = citations.groupBy(_.goldLabel.get).mapValues(_.size)
    println("%d ids appeared more than once in the data, %d were unique"
      .format(citCounts.filter(_._2 > 1).keySet.size,
        citCounts.filter(_._2 == 1).keySet.size))
        //citCounts.filter(_._2 > 1).map(_._1 + "\t" + _._2).mkString("\n")))
    citCounts foreach println
    val corefPred = citations.par.map(cit => cit.goldLabel.get -> corefTitleHash(cit.rawTitle)).seq
      //.groupBy(_._1).map(_._2).map(_.map(_._2.id)).zipWithIndex.flatMap { case (cluster, idx) =>
      //cluster.map(_ -> idx.toString)
    //}
    println("Finished Prediction")

    val corefPredMap = corefPred.toMap

    println(corefPred.groupBy(_._1).filter(_._2.size > 1))

    val filteredPrediction = corefPredMap.filter{case (k,_) => goldLabels.contains(k)}
    val filteredGold = goldLabels.filter{case(k,_) => corefPredMap.contains(k)}

    println("We filtered %d of %d mentions from prediction and %d of %d from gold".format(corefPredMap.keySet.size - filteredPrediction.keySet.size, corefPredMap.keySet.size, goldLabels.keySet.size - filteredGold.keySet.size, goldLabels.keySet.size))

    val pred = new BasicEvaluatableClustering(filteredPrediction)
    val gold = new BasicEvaluatableClustering(filteredGold)

    println(EvaluatableClustering.evaluationString(pred, gold))
  }
}

