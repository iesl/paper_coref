package org.allenai.scholar.paper_coref.data_structures

import java.util.UUID

import org.allenai.scholar.paper_coref._
import org.allenai.scholar.paper_coref.evaluation.{Alignment, Alignable}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.{read, write}

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

