package org.allenai.scholar.paper_coref.load

import java.io.{File, FileInputStream, InputStreamReader}

import org.allenai.scholar.paper_coref.{ParsedPaper, LocatedCitation, RawCitation, FileExtras}

import scala.xml.{NodeSeq, Elem, XML}

object LoadGrobid {

  def fromDir(dir: File, codec: String = "ISO-8859-1", fileFilter: File => Boolean = _ => true): Iterable[ParsedPaper] = dir.listFiles().filter(fileFilter).flatMap(fromFile(_,codec))
  
  def fromFilename(filename:String, codec: String = "ISO-8859-1"): Option[ParsedPaper] = fromFile(new File(filename),codec)
  
  def fromFile(file: File, codec: String = "ISO-8859-1"): Option[ParsedPaper] = {
    val xml = XML.load(new InputStreamReader(new FileInputStream(file), codec))
    val paperId = file.getNameWithoutExtension
    val headerCitation = loadHeader(xml).map(LocatedCitation(_, None, Some(paperId)))
    val bib = loadReferences(xml).map(LocatedCitation(_,None,None))
    if (headerCitation.isDefined) 
      Some(ParsedPaper.fromCitations(bib ++ Iterable(headerCitation.get)))
    else
      None
  }
  
  def fromFiles(headerFile: File, referencesFile: File, codec: String = "ISO-8859-1"): Option[ParsedPaper] = {
    assert(headerFile.getNameWithoutExtension == referencesFile.getNameWithoutExtension)
    val paperId = headerFile.getNameWithoutExtension
    val headerCitation = loadHeader(XML.load(new InputStreamReader(new FileInputStream(headerFile), codec))).map(LocatedCitation(_, None, Some(paperId)))
    val bib = loadReferences(XML.load(new InputStreamReader(new FileInputStream(referencesFile), codec))).map(LocatedCitation(_,None,None))
    if (headerCitation.isDefined)
      Some(ParsedPaper.fromCitations(bib ++ Iterable(headerCitation.get)))
    else
      None    
  }
  
  def loadHeader(xml: Elem): Option[RawCitation] = {
    val header = xml \ "teiHeader"
    val title = (header \\ "titleStmt" \\ "title").text
    val authors = getAuthors(header)
    val date = (header \\ "publicationStmt"  \\ "date").text // TODO: There seems to be several options here. Grobid even seems to do some cleaning of this field in other places.
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
  
  private def getAuthors(nodeSeq: NodeSeq) = (nodeSeq \\ "author").map(_ \\ "persName").map(_ \ "_").map(_.map(_.text.trim).mkString(" ")).filterNot(_.isEmpty)
  
  def loadReferences(xml: Elem) =
    (xml \\ "biblStruct").flatMap(loadReference)

  
  def loadReference(biblStruct: NodeSeq): Option[RawCitation] = {
    val title = (biblStruct \\ "analytic" \\ "title").text
    val authors = getAuthors(biblStruct)
    val date = (biblStruct \\ "date" \\ "@when").text
    val citation = RawCitation(title,authors.toList,date)
    if (citation.isEmpty) None else Some(citation)
  }
  
}
