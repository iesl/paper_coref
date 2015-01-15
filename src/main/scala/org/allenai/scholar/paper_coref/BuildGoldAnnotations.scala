package org.allenai.scholar.paper_coref

import cc.factorie.app.strings
import java.io.{BufferedWriter, FileWriter}
import scala.collection.mutable


case class GoldCitationDoc(doc:PaperMetadata, citations:Iterable[PaperMetadata])
/**
 * @author John Sullivan
 */
object BuildGoldAnnotations {
  def scoreEdge(reference:String, observed:String):Double = -strings.editDistance(reference, observed).toDouble

  def labelCitations(rawDocs:Iterable[ParsedPaper], paperMetadata:Iterable[PaperMetadata], bareCitations:Iterable[BareCitation]):Iterable[Citation] = {
    val metadataMap = paperMetadata.map(m => m.id -> m).toMap

    val goldCitDocs = bareCitations.groupBy(_.from).flatMap{ case (k, vs) =>
      metadataMap.get(k).map(pm => GoldCitationDoc(pm, vs.flatMap(v => metadataMap.get(v.to))))
    }

    val idToPred = rawDocs.map(pp => pp.selfCit.foundInPaperId -> pp).toMap
    val idToGold = goldCitDocs.map(gd => gd.doc.id -> gd).toMap

    val paperAlignment = (idToGold.keySet intersect idToPred.keySet) map { k =>
      idToPred(k) -> idToGold(k)
    }

    println(idToPred.keySet.--(idToGold.keySet).size + " ids in parse that were not in gold")
    println(idToGold.keySet.--(idToPred.keySet).size + " ids in gold that were not in parse")

    //val predPapers = rawDocs.map{ pd => Alignable(pd.selfCit.rawTitle, pd) }
    //val goldPapers = goldCitDocs.map{ gcd => Alignable(gcd.doc.title, gcd)}


    //val paperAlignment = Alignment.align(predPapers, goldPapers, scoreEdge)

    paperAlignment.flatMap { case (parsedDoc, goldDoc) =>
      val predBib = parsedDoc.bib.map(rc => Alignable(rc.rawTitle, rc))
      val goldBib = goldDoc.citations.map(gc => Alignable(gc.title, gc))

      Alignment.align(predBib, goldBib, scoreEdge).toSeq.map { case (predCit, paperMeta) =>
        predCit withGoldLabel paperMeta.id
      }.+:(parsedDoc.selfCit withGoldLabel goldDoc.doc.id)
    }
  }

  def main(args:Array[String]) {
    val pm = PaperMetadata fromFile args(0)
    println("Loaded metadata")
    val cg = BareCitation fromFile args(1)
    println("Loaded citation graph")
    val cits = ParsedPaper.fromCitations(CollapseCitations.fromDir(args(2)))
    println("Loaded citations")
    val wrt = new BufferedWriter(new FileWriter(args(3)))

    println("Loaded %d papers with a total of %d bib citations".format(cits.size, cits.map(_.bib.size).sum))
    val totalCits = cits.map(c => c.bib.size + 1).sum
    val emptyTitles = cits.map(c => c.bib.count(_.rawTitle.trim.isEmpty) + (if(c.selfCit.rawTitle.trim.isEmpty) 1 else 0)).sum
    val emptyAuthors = cits.map(c => c.bib.count(cit => cit.rawAuthors.isEmpty || cit.rawAuthors.forall(_.trim.isEmpty)) + (if(c.selfCit.rawAuthors.isEmpty || c.selfCit.rawAuthors.forall(_.trim.isEmpty)) 1 else 0)).sum
    println("Out of a total of %d citations, %d have empty titles (%.3f percent) and %d have empty authors (%.3f percent)"
      .format(totalCits, emptyTitles, (emptyTitles.toDouble/totalCits)/100, emptyAuthors, (emptyAuthors.toDouble/totalCits)/100))

    val labelled = labelCitations(cits, pm, cg)

    println ("Labelled citations")

    labelled.zipWithIndex foreach { case(cit, idx) =>
      wrt write  cit.toJsonString
      wrt.newLine()
      if(idx % 1000 == 0) {
        println("Wrote %d lines".format(idx))
        wrt.flush()
      }
    }
    wrt.flush()
    wrt.close()
    println("Writing complete")
  }

}

