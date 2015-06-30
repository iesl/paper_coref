package org.allenai.scholar.paper_coref.load

import java.io.File

import org.allenai.scholar.paper_coref.data_structures.ParsedPaper

import scala.collection.mutable.ArrayBuffer

/**
 * Base trait for loading input data to the coreference algorithm. * 
 */
trait Loader {

  /**
   * Specifies the format of the input. * 
   */
  val formatType: FormatType

  /**
   * Loads all of the ParsedPaper objects represented in the files in the input directory. * 
   * @param dir - the input directory
   * @param codec - the encoding of the files
   * @param fileFilter -  an optional filter of files used. The default is to use all files.
   * @return - an iterable of ParsedPaper objects.
   */
  def fromDir(dir: File, codec: String = "UTF-8", fileFilter: File => Boolean = _ => true): Iterable[ParsedPaper] = fromFiles(dir.listFiles().filter(fileFilter),codec)

  /**
   * Loads all of the ParsedPaper objects from the given files. Each file may contain zero, one or more ParsedPapers.
   * The function prints out error messages for the files which fail to load. These failures can be caused by malformed
   * XML, invalid JSON, etc.  
   * @param files - the files to use
   * @param codec - the encoding of the files 
   * @return
   */
  def fromFiles(files: Iterable[File], codec: String = "UTF-8"): Iterable[ParsedPaper] = {
    val numFiles = files.size
    val errors = new ArrayBuffer[String]()
    val res = files.zipWithIndex.flatMap{case (p,idx) =>
      print(s"\r[Loader] Loading from ${p.getName} (Loaded: ${idx+1}/$numFiles, Num Errors: ${errors.length}).")
      try {
        fromFile(p,codec)
      } catch {
        case e: Exception =>
          errors += p.getName
          println(" ERROR: " + e.getMessage)
          None
      }
    }
    println()
    res
  }

  /**
   * Load the ParsedPaper(s) from the file with the given name  
   * @param filename - the filename
   * @param codec - the encoding
   * @return - zero, one or more ParsedPapers
   */
  def fromFilename(filename:String, codec: String = "UTF-8"): Iterable[ParsedPaper] = fromFile(new File(filename),codec)

  /**
   * Load the ParsedPaper(s) from the given file 
   * @param file - input file
   * @param codec - encoding
   * @return - zero, one or more ParsedPapers
   */
  def fromFile(file: File, codec: String = "UTF-8"): Iterable[ParsedPaper]

  /**
   * Load a ParsedPaper object, such that the header is stored in the given header file and the 
   * references in the reference file.* * 
   * @param headerFile - the file storing the header information.
   * @param referencesFile - the file storing the reference information.
   * @param codec - the encoding.
   * @return - a parsed paper or None if it could not be loaded.
   */
  def fromSeparateHeaderAndReferenceFile(headerFile: File, referencesFile: File, codec: String = "UTF-8"): Option[ParsedPaper]

  /**
   * Loads parsed papers from a collection of headers and references file pairs.
   * @param headersAndReferences - header and reference file pairs
   * @param codec - the encoding
   * @return - the parsed paper from the given files
   */
  def fromSeparateFiles(headersAndReferences: Iterable[(File,File)], codec: String = "UTF-8"): Iterable[ParsedPaper] = {
    val numFiles = headersAndReferences.size
    val errors = new ArrayBuffer[String]()
    val res = headersAndReferences.zipWithIndex.flatMap{case (p,idx) =>
      print(s"\r[Loader] Loading from ${p._1.getName} and ${p._2.getName} (Loaded: ${idx+1}/$numFiles, Num Errors: ${errors.length}).")
      try {
        fromSeparateHeaderAndReferenceFile(p._1,p._2,codec)
      } catch {
        case e: Exception =>
          errors += p._1.getName
          errors += p._2.getName
          println(" ERROR: " + e.getMessage)
          None
      }
    }
    println()
    res
  }
}

/**
 * Convenience methods for the loader.* 
 */
object Loader {
  
  // All of the loaders in the system
  private val allLoaders = Iterable(LoadGrobid,LoadParsCit,LoadRPP,LoadLocatedCitations,LoadPaperMetadata,LoadCora).map((ldr) => ldr.formatType -> ldr).toMap[FormatType,Loader]

  /**
   * Given the formatType return the loader which is known to load that type. 
   * For example, a format type of GrobidFormat returns LoadGrobid.  
   * @param formatType - the input data format
   * @return - the loader to load that format.
   */
  def apply(formatType: FormatType) = allLoaders(formatType)

  /**
   * Given the string version of the formatType return the 
   * loader which is known to load that type.
   * @param string - the input data format as a string.
   * @return - the loader to load that format.
   */
  def apply(string: String) = allLoaders(FormatType(string))
}