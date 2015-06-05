package org.allenai.scholar.paper_coref.load

import java.io.{FileInputStream, InputStreamReader, File}

import org.allenai.scholar.paper_coref.{RawCitation, LocatedCitation, ParsedPaper, FileExtras}

import scala.xml.{NodeSeq, Elem, XML}

trait XMLLoader {

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

  def loadHeader(xml: Elem): Option[RawCitation]
  
  def loadReferences(xml: Elem):Iterable[RawCitation]

  def loadReference(xml: NodeSeq): Option[RawCitation]
  
}
