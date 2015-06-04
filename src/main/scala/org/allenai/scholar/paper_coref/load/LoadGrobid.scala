package org.allenai.scholar.paper_coref.load

import java.io.{File, FileInputStream, InputStreamReader}

import org.allenai.scholar.paper_coref.{ParsedPaper, LocatedCitation, RawCitation, FileExtras}

import scala.xml.{NodeSeq, Elem, XML}

object LoadGrobid {

  def fromFilename(filename:String, codec: String = "ISO-8859-1"): ParsedPaper = fromFile(new File(filename),codec)
  
  def fromFile(file: File, codec: String = "ISO-8859-1"): ParsedPaper = {
    val xml = XML.load(new InputStreamReader(new FileInputStream(file), codec))
    val paperId = file.getNameWithoutExtension
    val headerCitation = LocatedCitation(loadHeader(xml),None,Some(paperId))
    val bib = loadReferences(xml).map(LocatedCitation(_,None,None))
    ParsedPaper.fromCitations(bib ++ Iterable(headerCitation))
  }
  
  def loadHeader(xml: Elem): RawCitation = {
    val header = xml \ "teiHeader"
    val title = (header \\ "titleStmt" \\ "title").text
    val authors = getAuthors(header)
    val date = (header \\ "publicationStmt"  \\ "date").text // TODO: There seems to be several options here. Grobid even seems to do some cleaning of this field in other places.
    RawCitation(title,authors.toList,date)
  }
  
  private def getAuthors(nodeSeq: NodeSeq) = (nodeSeq \\ "author").map(_ \\ "persName").map(_ \ "_").map(_.map(_.text).mkString(" "))
  
  def loadReferences(xml: Elem) =
    (xml \\ "bibleStruct").map(loadReference)

  
  def loadReference(biblStruct: NodeSeq): RawCitation = {
    val title = (biblStruct \\ "analytic" \\ "title").text
    val authors = getAuthors(biblStruct)
    val date = (biblStruct \\ "date" \\ "@when").text
    RawCitation(title,authors.toList,date)
  }
  
}
