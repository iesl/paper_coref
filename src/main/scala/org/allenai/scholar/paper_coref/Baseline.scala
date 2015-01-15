package org.allenai.scholar.paper_coref

import cc.factorie.app.strings
import cc.factorie.app.nlp.segment.DeterministicTokenizer
import cc.factorie.app.nlp.Document
import org.allenai.scholar.paper_coref.citations.{ParsedPaper, LocatedCitation}
import java.io.File
import cc.factorie.util.{EvaluatableClustering, BasicEvaluatableClustering}
import java.util.UUID

/**
 * @author John Sullivan
 */

object Baseline {

  val fieldSep = Character.toString(31.toChar)
  def corefTitleHash(rawTitle:String):String =
    DeterministicTokenizer.process(new Document(rawTitle))
      .tokens.filterNot(_.isPunctuation).map(t => strings.porterStem(t.string.toLowerCase)).mkString(fieldSep)

  def main(args:Array[String]) {

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


    val paperMentions = PaperMention.generate(citsMap, goldCitDocs)


    val gold = new BasicEvaluatableClustering(paperMentions.map(p => p.id -> p.trueLabel))
    val pred = new BasicEvaluatableClustering(paperMentions.map(p => p.id -> corefTitleHash(p.title)))

    println(EvaluatableClustering.evaluationString(pred, gold))
  }
}

case class PaperMention(authors:Set[String], title:String, venue:String, date:String, trueLabel:String, isPaper:Boolean, goldData:Option[PaperMention]) {
  lazy val id:String = UUID.randomUUID().toString
}

object PaperMention {
  def generate(predCitDocs:Map[String, ParsedPaper], goldCitDocs:Map[String, GoldCitationDoc]):Iterable[PaperMention] = {
    (predCitDocs.keySet intersect goldCitDocs.keySet).flatMap { k =>
      val predPaperCit = predCitDocs(k)
      val goldPaperCit = goldCitDocs(k)
      val paperTitleMention = {
        val lc = predPaperCit.self
        val pm = goldPaperCit.doc
        PaperMention(lc.rawCitation.rawAuthors.toSet, lc.rawCitation.rawTitle, "", lc.rawCitation.date, pm.id, true,
          Some(PaperMention(pm.authors.toSet, pm.title, pm.venue, pm.year.toString, pm.id, true, None)))
      }

      val scoreDistPos = {(s1:String, s2:String) => (Seq(s1.size, s2.size).max - strings.editDistance(s1, s2)).toDouble}
      val sources = predPaperCit.bib.map(p => Alignable(p.rawCitation.rawTitle, p))
      val targets = goldPaperCit.citations.map(p => Alignable(p.title, p))
      Alignment.jaggedAlign(sources, targets, scoreDistPos).aligned.map { case (lc, pm) =>
        PaperMention(lc.rawCitation.rawAuthors.toSet, lc.rawCitation.rawTitle, "", lc.rawCitation.date, pm.id, false,
          Some(PaperMention(pm.authors.toSet, pm.title, pm.venue, pm.year.toString, pm.id, false, None)))
      } ++: Seq(paperTitleMention)
    }
  }
}
