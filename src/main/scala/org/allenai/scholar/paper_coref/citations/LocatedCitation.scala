package org.allenai.scholar.paper_coref.citations

import java.io.{File, BufferedReader, FileReader}
import cc.factorie._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.allenai.scholar.paper_coref.{Baseline, GoldCitationDoc, BareCitation, PaperMetadata}

object ParsedPaper {
  def fromCitations(cits:Iterable[LocatedCitation]):ParsedPaper = {
    assert(cits.count(_.paperId.isDefined) == 1)
    ParsedPaper(cits.filter(_.paperId.isDefined).head, cits.filterNot(_.paperId.isDefined))
  }
}

case class ParsedPaper(self:LocatedCitation, bib:Iterable[LocatedCitation])

case class RawCitation(rawTitle:String, rawAuthors:List[String], date:String)

case class LocatedCitation(rawCitation:RawCitation, citingPaperId:Option[String], paperId:Option[String]) {
  lazy val foundInId = citingPaperId.getOrElse(paperId.get)
}

object LocatedCitation {

  implicit val formats = DefaultFormats

  def fromFile(filename:String):Iterable[LocatedCitation] =
    new BufferedReader(new FileReader(filename)).toIterator.map{ line =>
      parse(line).extract[LocatedCitation]
  }.toIterable
}

object CitationMetrics extends App {
  val citsDir = args(0)
  val locCits = new File(citsDir).listFiles().flatMap(f => LocatedCitation.fromFile(f.getAbsolutePath)).toIterable

  val paperCit = {c:LocatedCitation => c.paperId.isDefined}
  val bibCit = {c:LocatedCitation => c.citingPaperId.isDefined}

  val emptyTitle = {c:LocatedCitation => c.rawCitation.rawTitle.trim.isEmpty}
  val emptyAuthor = {c:LocatedCitation => c.rawCitation.rawAuthors.isEmpty || c.rawCitation.rawAuthors.forall(_.trim.isEmpty)}

  println("This should always be 100%: " + locCits.percentWhere(c => (c.paperId.isDefined && c.citingPaperId.isEmpty) || (c.paperId.isEmpty && c.citingPaperId.isDefined)))

  println("Paper citations: " + locCits.percentWhere(paperCit))
  println("Bibliography citations: " + locCits.percentWhere(bibCit))

  println("Missing titles (empty string): " + locCits.percentWhere(emptyTitle))
  println("Missing authors (empty list or list of empty strings): " + locCits.percentWhere(emptyAuthor))

  println("Paper cits with missing titles: " + locCits.filter(paperCit).percentWhere(emptyTitle))
  println("Bib cits with missing titles: " + locCits.filter(bibCit).percentWhere(emptyTitle))

  println("Paper cits with missing authors: " + locCits.filter(paperCit).percentWhere(emptyAuthor))
  println("Bib cits with missing authors: " + locCits.filter(bibCit).percentWhere(emptyAuthor))

  val citsByPaper = locCits.groupBy(_.foundInId)

  println("Papers with exactly one paper cit: " + citsByPaper.percentWhere(_._2.count(_.paperId.isDefined) == 1))
  println("Papers with > 1 paper cit: " + citsByPaper.percentWhere(_._2.count(_.paperId.isDefined) > 1))
  println("Papers with 0 paper cits: " + citsByPaper.percentWhere(_._2.count(_.paperId.isDefined) == 0))

  val goldCitDocs = {
    val pms = PaperMetadata.fromFile(args(1))
    val bcs = BareCitation.fromFile(args(2))
    val pmMap = pms.map(p => p.id -> p).toMap
    bcs.groupBy(_.from).flatMap{ case (k, vs) =>
      pmMap.get(k).map(pm => GoldCitationDoc(pm, vs.flatMap(v => pmMap.get(v.to))))
    }.map(g => g.doc.id -> g).toMap
  }

  println("Found %d gold docs".format(goldCitDocs.keySet.size))

  println("Scraped papers aligned to gold: " + citsByPaper.keySet.percentWhere(c => goldCitDocs.contains(c)) + " of scraped")
  println("Scraped papers aligned to gold: " + goldCitDocs.keySet.percentWhere(c => citsByPaper.contains(c)) + " of gold")

  val alignedPapers = citsByPaper.keySet.intersect(goldCitDocs.keySet).map(k => ParsedPaper.fromCitations(citsByPaper(k)) -> goldCitDocs(k)).toIterable

  val titleMatchExact = {(pp:ParsedPaper, gp:GoldCitationDoc) => pp.self.rawCitation.rawTitle == gp.doc.title}
  val titleMatchDowncaseTrim = {(pp:ParsedPaper, gp:GoldCitationDoc) => pp.self.rawCitation.rawTitle.trim.toLowerCase.replaceAll("""\s+""", " ") == gp.doc.title.trim.toLowerCase.replaceAll("""\s+""", " ")}
  val titleMatchStemmed = {(pp:ParsedPaper, gp:GoldCitationDoc) => Baseline.corefTitleHash(pp.self.rawCitation.rawTitle) == Baseline.corefTitleHash(gp.doc.title)}

  println("Aligned papers title exact match: " + alignedPapers.percentWhere(titleMatchExact.tupled))
  println("Aligned papers title downcase trim match: " + alignedPapers.percentWhere(titleMatchDowncaseTrim.tupled))
  println("Aligned papers title stemmed match: " + alignedPapers.percentWhere(titleMatchStemmed.tupled))

  println("Aligned papers without empty titles title exact match: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchExact.tupled))
  println("Aligned papers without empty titles title downcase trim match: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchDowncaseTrim.tupled))
  println("Aligned papers without empty titles title stemmed match: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmed.tupled))
}




