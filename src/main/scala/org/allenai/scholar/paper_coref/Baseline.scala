package org.allenai.scholar.paper_coref

import cc.factorie.app.strings
import cc.factorie.app.nlp.segment.DeterministicTokenizer
import cc.factorie.app.nlp.Document
import java.io._
import cc.factorie.util.EvaluatableClustering
import java.util.UUID

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read, write}

trait PaperCoref {
  
  val name: String
  
  def performCoref(mentions:Iterable[PaperMention]):Iterable[Iterable[PaperMention]]
}

object PaperCoref {
  
  private val allPaperCorefAlgorithms = Iterable(Baseline,AlphaOnly).map((alg) => alg.name -> alg).toMap
  
  def apply(string: String) = allPaperCorefAlgorithms(string)
  
}

trait HashingCoref extends PaperCoref {
  
  val name = "HashingCoref"
  
  def titleHash(title:String):String

  def performCoref(mentions: Iterable[PaperMention]) = mentions.groupBy(m => if(m.title.nonEmpty) titleHash(m.title) else m.id).values
}

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

object AlphaOnly extends HashingCoref {

  override val name = "AlphaOnly"

  def titleHash(title: String) = title.replaceAll("""\s+""", " ").toLowerCase.replaceAll("""^[\w]""","")
}

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

case class PaperMention(id:String, authors:Set[String], title:String, venue:String, date:String, trueLabel:String, isPaper:Boolean, goldData:Option[PaperMention]) {
  implicit val formats = Serialization.formats(NoTypeHints)
  lazy val toJsonString = write(this)
}

object PaperMention {
  implicit val formats = Serialization.formats(NoTypeHints)

  def fromJsonString(js:String)= read[PaperMention](js)

  /**
   * *
   * @param predCitDocs - Mapping from paper ids to the parse paper objects 
   * @param goldCitDocs - Mapping from paper ids to gold citation doc
   * @return
   */
  def generate(predCitDocs:Map[String, ParsedPaper], goldCitDocs:Map[String, GoldCitationDoc]):Iterable[PaperMention] = {
    (predCitDocs.keySet intersect goldCitDocs.keySet).flatMap { k =>
      val predPaperCit = predCitDocs(k)
      val goldPaperCit = goldCitDocs(k)
      
      // Align the two title mentions.
      val paperTitleMention = {
        val lc = predPaperCit.self
        val pm = goldPaperCit.doc
        PaperMention(UUID.randomUUID().toString, lc.rawCitation.rawAuthors.toSet, lc.rawCitation.rawTitle, "", lc.rawCitation.date, pm.id, true,
          Some(PaperMention(pm.id, pm.authors.toSet, pm.title, pm.venue, pm.year.toString, pm.id, true, None)))
      }

      // Align the references.
      val sources = predPaperCit.bib.map(p => Alignable(p.rawCitation.rawTitle, p))
      val targets = goldPaperCit.citations.map(p => Alignable(p.title, p))
      Alignment.jaggedAlign(sources, targets, Alignment.scoreDistPos).aligned.map { case (lc, pm) =>
        PaperMention(UUID.randomUUID().toString, lc.rawCitation.rawAuthors.toSet, lc.rawCitation.rawTitle, "", lc.rawCitation.date, pm.id, false,
          Some(PaperMention(pm.id, pm.authors.toSet, pm.title, pm.venue, pm.year.toString, pm.id, false, None)))
      } ++: Seq(paperTitleMention)
    }
  }
}

