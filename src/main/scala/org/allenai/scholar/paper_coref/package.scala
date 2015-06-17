package org.allenai.scholar

import java.io.File

import cc.factorie.util.BasicEvaluatableClustering
import org.allenai.scholar.paper_coref.data_structures.PaperMention

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

}
