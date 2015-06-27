package org.allenai.scholar.paper_coref.load

import java.io.{FileInputStream, InputStreamReader, BufferedReader, File}

import org.allenai.scholar.paper_coref.data_structures.{LocatedCitation, RawCitation, PaperMetadata, ParsedPaper}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import cc.factorie._
import org.allenai.scholar.paper_coref._

/**
 * Loader for JSON serialized PaperMetadata data.
 * Note that data must be stored such that each JSON
 * object appears on a single line.
 */
object LoadPaperMetadata extends Loader {
  override val formatType: FormatType = PaperMetadataFormat
  
  implicit val formats = DefaultFormats

  override def fromFile(file: File, codec: String): Iterable[ParsedPaper] = {
    val rawCitations = getPaperMetadata(file,codec).map(RawCitation.fromPaperMetadata)
    if (rawCitations.size >= 1) {
      val id = file.getNameWithoutExtension
      val self = LocatedCitation(rawCitations.head,None,Some(id))
      val cits = rawCitations.drop(1).map(LocatedCitation(_,Some(id),None))
      ParsedPaper.fromCitationsSafe(Iterable(self) ++ cits)
    } else {
      Iterable.empty
    }
  }

  override def fromSeparateFiles(headerFile: File, referencesFile: File, codec: String): Option[ParsedPaper] =  throw new UnsupportedOperationException

  private def getPaperMetadata(file:File,codec:String):Iterable[PaperMetadata] =
    new BufferedReader(new InputStreamReader(new FileInputStream(file),codec)).toIterator.map{ line =>
      parse(line).extract[PaperMetadata]
    }.toIterable
}
