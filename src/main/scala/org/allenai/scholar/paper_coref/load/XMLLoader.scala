package org.allenai.scholar.paper_coref.load

import java.io.{File, FileInputStream, InputStreamReader}

import org.allenai.scholar.paper_coref.data_structures.{RawCitation, ParsedPaper, LocatedCitation}
import org.allenai.scholar.paper_coref.FileExtras

import scala.xml.{Elem, NodeSeq, XML}

trait XMLLoader extends Loader {

  def fromFile(file: File, codec: String = "ISO-8859-1"): Option[ParsedPaper] = {
    val xml = XML.load(new InputStreamReader(new FileInputStream(file), codec))
    val paperId = file.getNameWithoutExtension
    val headerCitation = loadHeader(xml).map(LocatedCitation(_, None, Some(paperId)))
    val bib = loadReferences(xml).map(LocatedCitation(_,Some(paperId),None))
    if (headerCitation.isDefined)
      Some(ParsedPaper.fromCitations(bib ++ Iterable(headerCitation.get)))
    else
      None
  }

  def fromSeparateFiles(headerFile: File, referencesFile: File, codec: String = "ISO-8859-1"): Option[ParsedPaper] = {
    assert(headerFile.getNameWithoutExtension == referencesFile.getNameWithoutExtension)
    val paperId = headerFile.getNameWithoutExtension
    val headerCitation = loadHeader(XML.load(new InputStreamReader(new FileInputStream(headerFile), codec))).map(LocatedCitation(_, None, Some(paperId)))
    val bib = loadReferences(XML.load(new InputStreamReader(new FileInputStream(referencesFile), codec))).map(LocatedCitation(_,Some(paperId),None))
    if (headerCitation.isDefined)
      Some(ParsedPaper.fromCitations(bib ++ Iterable(headerCitation.get)))
    else
      None
  }

  def loadHeader(xml: Elem): Option[RawCitation]
  
  def loadReferences(xml: Elem):Iterable[RawCitation]

  def loadReference(xml: NodeSeq): Option[RawCitation]
  
}
