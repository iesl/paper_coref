package org.allenai.scholar.paper_coref.load

import java.io.{File, FileInputStream, InputStreamReader}

import cc.factorie.util.NonValidatingXML
import org.allenai.scholar.paper_coref.FileExtras
import org.allenai.scholar.paper_coref.data_structures.{LocatedCitation, ParsedPaper, RawCitation}

import scala.xml.{Elem, NodeSeq}

/**
 * A trait for loaders which operate on XML data. Breaks down the loading process 
 * into loading Headers and References  
 */
trait XMLLoader extends Loader {

  def fromFile(file: File, codec: String = "UTF-8"): Iterable[ParsedPaper] = {
    val xml = NonValidatingXML.load(new InputStreamReader(new FileInputStream(file), codec))
    val paperId = file.getNameWithoutExtension
    val headerCitation = loadHeader(xml).map(LocatedCitation(_, None, Some(paperId)))
    val bib = loadReferences(xml).map(LocatedCitation(_,Some(paperId),None))
    if (headerCitation.isDefined)
      Iterable(ParsedPaper.fromCitations(bib ++ Iterable(headerCitation.get)))
    else
      Iterable.empty
  }

  def fromSeparateHeaderAndReferenceFile(headerFile: File, referencesFile: File, codec: String = "UTF-8"): Option[ParsedPaper] = {
    assert(headerFile.getNameWithoutExtension == referencesFile.getNameWithoutExtension)
    val paperId = headerFile.getNameWithoutExtension
    val headerCitation = loadHeader(NonValidatingXML.load(new InputStreamReader(new FileInputStream(headerFile), codec))).map(LocatedCitation(_, None, Some(paperId)))
    val bib = loadReferences(NonValidatingXML.load(new InputStreamReader(new FileInputStream(referencesFile), codec))).map(LocatedCitation(_,Some(paperId),None))
    if (headerCitation.isDefined)
      Some(ParsedPaper.fromCitations(bib ++ Iterable(headerCitation.get)))
    else
      None
  }

  /**
   * Load the header (if possible) from the XML element.  
   * @param xml - the xml to load from
   * @return - the RawCitation stored in the XML
   */
  def loadHeader(xml: Elem): Option[RawCitation]

  /**
   * Load the references from the XML element. 
   * @param xml - the xml to load from
   * @return - all of the reference citations.
   */
  def loadReferences(xml: Elem):Iterable[RawCitation]

  /**
   * Loads a single reference from the given XML*
   * @param xml - xml to load from
   * @return - the  reference listed in the xml.
   */
  def loadReference(xml: NodeSeq): Option[RawCitation]
  
}
