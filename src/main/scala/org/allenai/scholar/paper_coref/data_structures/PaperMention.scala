package org.allenai.scholar.paper_coref.data_structures

import java.util.UUID

import org.allenai.scholar.paper_coref.evaluation.{Alignable, Alignment}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read


/**
 * A representation of a paper mention used by the coreference algorithms. 
 * @param id - a unique id for the mention
 * @param authors - the authors of the paper 
 * @param title - the title of the paper
 * @param venue - the venue the paper appears in
 * @param date - the date published
 * @param trueLabel - the id of the gold clustering
 * @param isPaper - true iff the paper was a header extraction
 * @param goldData - the corresponding gold mention data
 */

case class PaperMention(id:String, authors:Set[String], title:String, venue:String, date:String, trueLabel:String, isPaper:Boolean, goldData:Option[PaperMention]) extends JSONSerializable

/**
 * Utilities for the PaperMention class  
 */
object PaperMention {
  implicit val formats = Serialization.formats(NoTypeHints)

  /**
   * Loads the PaperMention from JSON
   * @param js - the json string
   * @return - deserialized PaperMention
   */
  def fromJsonString(js:String)= read[PaperMention](js)

  /**
   * Creates a set of paper mentions by aligning extracted ParsedPaper objects using the edit distance between title fields.
   * @param predCitDocs - Mapping from paper ids to the parsed paper objects
   * @param goldCitDocs - Mapping from paper ids to gold citation doc
   * @return - paper mentions for coreferences.
   */
  def generate(predCitDocs:Map[String, ParsedPaper], goldCitDocs:Map[String, GoldCitationDoc]):Iterable[PaperMention] = {
    (predCitDocs.keySet intersect goldCitDocs.keySet).flatMap { k =>
      val predPaperCit = predCitDocs(k)
      val goldPaperCit = goldCitDocs(k)
      
      // Align the two title mentions.
      val paperTitleMention = {
        val lc = predPaperCit.self
        val pm = goldPaperCit.doc
        PaperMention(UUID.randomUUID().toString, lc.rawCitation.rawAuthors.toSet, lc.rawCitation.rawTitle, lc.rawCitation.venue, lc.rawCitation.date, pm.id, true,
          Some(PaperMention(pm.id, pm.authors.toSet, pm.title, pm.venue, pm.year.toString, pm.id, true, None)))
      }

      // Align the references.
      val sources = predPaperCit.bib.map(p => Alignable(p.rawCitation.rawTitle, p))
      val targets = goldPaperCit.citations.map(p => Alignable(p.title, p))
      Alignment.jaggedAlign(sources, targets, Alignment.scoreDistPos).aligned.map { case (lc, pm) =>
        PaperMention(UUID.randomUUID().toString, lc.rawCitation.rawAuthors.toSet, lc.rawCitation.rawTitle, lc.rawCitation.venue, lc.rawCitation.date, pm.id, false,
          Some(PaperMention(pm.id, pm.authors.toSet, pm.title, pm.venue, pm.year.toString, pm.id, false, None)))
      } ++: Seq(paperTitleMention)
    }
  }
}

