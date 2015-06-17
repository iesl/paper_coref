package org.allenai.scholar.paper_coref.coreference

import java.io.File

import cc.factorie.app.nlp.Document
import cc.factorie.app.nlp.segment.DeterministicTokenizer
import cc.factorie.app.strings
import cc.factorie.util.EvaluatableClustering
import org.allenai.scholar.paper_coref._


object Baseline extends HashingCoref {

  override val name = "Baseline"

  val fieldSep = Character.toString(31.toChar)
  def titleHash(rawTitle:String):String =
    DeterministicTokenizer.process(new Document(rawTitle))
      .tokens.filterNot(_.isPunctuation).map(t => strings.porterStem(t.string.toLowerCase)).mkString(fieldSep)


  //def performCoref(mentions: Iterable[PaperMention]) = mentions.groupBy(m => if(m.title.nonEmpty) corefTitleHash(m.title) else m.id).values

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


    val paperMentions = PaperMention.generate(citsMap, goldCitDocs)//.filterNot(_.title.trim.isEmpty)

    val prediction = performCoref(paperMentions)

    val gold = prediction.trueClustering
    val pred = prediction.predictedClustering

    println(EvaluatableClustering.evaluationString(pred, gold))
  }
}
