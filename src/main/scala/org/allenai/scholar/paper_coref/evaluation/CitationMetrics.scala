package org.allenai.scholar.paper_coref.evaluation

import java.io.File

import cc.factorie.app.strings
import org.allenai.scholar.paper_coref.coreference.Baseline
import org.allenai.scholar.paper_coref.data_structures._
import org.allenai.scholar.paper_coref._

object CitationMetrics extends App {
 printStatistics(args(0))

  def printStatistics(citationDir: String): Unit = printStatistics(new File(citationDir).listFiles().flatMap(f => LocatedCitation.fromFile(f.getAbsolutePath)).toIterable)

  def printStatistics(locatedCitations: Iterable[LocatedCitation]): Unit = {

    val paperCit = {c:LocatedCitation => c.paperId.isDefined}
    val bibCit = {c:LocatedCitation => c.citingPaperId.isDefined}

    val emptyTitle = {c:LocatedCitation => c.rawCitation.rawTitle.trim.isEmpty}
    val emptyAuthor = {c:LocatedCitation => c.rawCitation.rawAuthors.isEmpty || c.rawCitation.rawAuthors.forall(_.trim.isEmpty)}

    println("This should always be 100%: " + locatedCitations.percentWhere(c => (c.paperId.isDefined && c.citingPaperId.isEmpty) || (c.paperId.isEmpty && c.citingPaperId.isDefined)))

    println("Paper citations: " + locatedCitations.percentWhere(paperCit))
    println("Bibliography citations: " + locatedCitations.percentWhere(bibCit))

    println("Missing titles (empty string): " + locatedCitations.percentWhere(emptyTitle))
    println("Missing authors (empty list or list of empty strings): " + locatedCitations.percentWhere(emptyAuthor))

    println("Paper cits with missing titles: " + locatedCitations.filter(paperCit).percentWhere(emptyTitle))
    println("Bib cits with missing titles: " + locatedCitations.filter(bibCit).percentWhere(emptyTitle))

    println("Paper cits with missing authors: " + locatedCitations.filter(paperCit).percentWhere(emptyAuthor))
    println("Bib cits with missing authors: " + locatedCitations.filter(bibCit).percentWhere(emptyAuthor))

    val citsByPaper = locatedCitations.groupBy(_.foundInId)

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

    val alignedPapers = citsByPaper.keySet.intersect(goldCitDocs.keySet).map(k => ParsedPaper.fromCitations(citsByPaper(k)) -> goldCitDocs(k)).toSeq

    val titleStringMatchExact = {(s1:String, s2:String) => s1 == s2}
    val titleStringMatchDowncaseTrim = {(s1:String, s2:String) => s1.trim.toLowerCase.replaceAll("""\s+""", " ") == s2.trim.toLowerCase.replaceAll("""\s+""", " ")}
    val titleStringMatchStemmed = {(s1:String, s2:String) => Baseline.titleHash(s1) == Baseline.titleHash(s2)}

    val titleMatchExact = {(pp:ParsedPaper, gp:GoldCitationDoc) => pp.self.rawCitation.rawTitle == gp.doc.title}
    val titleMatchDowncaseTrim = {(pp:ParsedPaper, gp:GoldCitationDoc) => pp.self.rawCitation.rawTitle.trim.toLowerCase.replaceAll("""\s+""", " ") == gp.doc.title.trim.toLowerCase.replaceAll("""\s+""", " ")}
    val titleMatchStemmed = {(pp:ParsedPaper, gp:GoldCitationDoc) => Baseline.titleHash(pp.self.rawCitation.rawTitle) == Baseline.titleHash(gp.doc.title)}

    def titleMatchStemmedEditDistThreshold(threshold:Int):((ParsedPaper, GoldCitationDoc) => Boolean) = {
      {(pp:ParsedPaper, gp:GoldCitationDoc) => strings.editDistance(Baseline.titleHash(pp.self.rawCitation.rawTitle), Baseline.titleHash(gp.doc.title)) < threshold}
    }

    println("Aligned papers title exact match: " + alignedPapers.percentWhere(titleMatchExact.tupled))
    println("Aligned papers title downcase trim match: " + alignedPapers.percentWhere(titleMatchDowncaseTrim.tupled))
    println("Aligned papers title stemmed match: " + alignedPapers.percentWhere(titleMatchStemmed.tupled))

    println("Aligned papers without empty titles title exact match: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchExact.tupled))
    println("Aligned papers without empty titles title downcase trim match: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchDowncaseTrim.tupled))
    println("Aligned papers without empty titles title stemmed match: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmed.tupled))
    //println("Aligned papers without empty titles title stemmed threshold 1: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmedEditDistThreshold(1).tupled))
    //println("Aligned papers without empty titles title stemmed threshold 2: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmedEditDistThreshold(2).tupled))
    //println("Aligned papers without empty titles title stemmed threshold 3: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmedEditDistThreshold(3).tupled))
    //println("Aligned papers without empty titles title stemmed threshold 4: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmedEditDistThreshold(4).tupled))
    //println("Aligned papers without empty titles title stemmed threshold 5: " + alignedPapers.filterNot(i => emptyTitle(i._1.self)).percentWhere(titleMatchStemmedEditDistThreshold(5).tupled))


    println("Aligned Papers: " + alignedPapers.size)

    println("Aligned Papers with empty predicted bibs: " + alignedPapers.percentWhere(_._1.bib.isEmpty))
    println("Aligned Papers with empty gold bibs: " + alignedPapers.percentWhere(_._2.citations.isEmpty))
    println("Aligned papers with empty gold and predicted bibs: " + alignedPapers.percentWhere{case (a, b) => a.bib.isEmpty && b.citations.isEmpty})
    println("Aligned papers whose predicted bibs all have empty titles: " + alignedPapers.percentWhere(_._1.bib.forall(emptyTitle)))

    val alignedCitations = alignedPapers.toSeq.map { case (pred, gold) =>
      val sources = pred.bib.map(p => Alignable(p.rawCitation.rawTitle, p))
      val targets = gold.citations.map(p => Alignable(p.title, p))
      //println("About to align %d sources with %d targets".format(sources.size, targets.size))
      Alignment.jaggedAlign(sources, targets, Alignment.scoreDistPos)
    }

    println("Aligned cits: " + alignedCitations.size)
    println ("After alignment, empty alignments: " + alignedCitations.percentWhere{case AlignResult(a, _, _) => a.isEmpty})

    val citTitles = alignedCitations.flatMap { case AlignResult(aligned,_,_) => aligned }.map { case(lc, pm) =>
      lc.rawCitation.rawTitle -> pm.title
    }

    println("Aligned bib citations title exact match: " + citTitles.percentWhere(titleStringMatchExact.tupled))
    println("Aligned bib citations title downcase trim match: " + citTitles.percentWhere(titleStringMatchDowncaseTrim.tupled))
    println("Aligned bib citations title stemmed match: " + citTitles.percentWhere(titleStringMatchStemmed.tupled))
    println("Nonempty Aligned bib citations title exact match: " + citTitles.filterNot(t => t._1.isEmpty || t._2.isEmpty).percentWhere(titleStringMatchExact.tupled))
    println("Nonempty Aligned bib citations title downcase trim match: " + citTitles.filterNot(t => t._1.isEmpty || t._2.isEmpty).percentWhere(titleStringMatchDowncaseTrim.tupled))
    println("Nonempty Aligned bib citations title stemmed match: " + citTitles.filterNot(t => t._1.isEmpty || t._2.isEmpty).percentWhere(titleStringMatchStemmed.tupled))

  }
}
