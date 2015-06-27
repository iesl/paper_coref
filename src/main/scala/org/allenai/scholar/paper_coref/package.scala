package org.allenai.scholar

import java.io.{FileReader, BufferedReader, File}

import cc.factorie.util.BasicEvaluatableClustering
import org.allenai.scholar.paper_coref.data_structures.PaperMention
import cc.factorie._

/**
 * @author John Sullivan
 */
package object paper_coref {

  case class Percent(num:Int, denom:Int) {
    lazy val value = (num.toDouble / denom)*100
    override def toString = s"$num of $denom ($value %)"
  }

  implicit class IterableExtras[A](coll:Iterable[A]) {
    def percentWhere(pred:A => Boolean):Percent = {
      val denom = coll.size
      val num = coll.count(pred)
      Percent(num, denom)
    }
  }

  implicit class PaperMentionIterableExtras(pms:Iterable[Iterable[PaperMention]]) {
    def predictedClustering = new BasicEvaluatableClustering(pms.zipWithIndex.flatMap{case (cluster, idx) => cluster.map(_.id -> idx.toString)})
    def trueClustering = new BasicEvaluatableClustering(pms.flatten.map(m => m.id -> m.trueLabel))
  }
  
  implicit class FileExtras(file: File) {
    def getNameWithoutExtension = {
      val split = file.getName.split("\\.")
      if (split.length == 1)
        split(0)
      else
        split.slice(0,1).mkString("")
    }
  }

  implicit class StringExtras(string: String) {
    def removeNewlines =
      string.replaceAll("\n|\r","")
  }

  def parseExperimentInput(input: List[String], inputType: String) = {
    input.flatMap((f) =>
      if (inputType.equalsIgnoreCase("directory"))
        new File(f).listFiles()
      else if (inputType.equalsIgnoreCase("file of filenames")) {
        new BufferedReader(new FileReader(f)).toIterator.map(new File(_)).toIterable
      } else if (inputType.equalsIgnoreCase("file")) {
        Iterable(new File(f))
      } else
        throw new Exception(s"Unknown input type: $inputType. Must be: directory,file of filenames, file")
    )
  }
}
