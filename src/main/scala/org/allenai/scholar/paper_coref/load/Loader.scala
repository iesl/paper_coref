package org.allenai.scholar.paper_coref.load

import java.io.File

import org.allenai.scholar.paper_coref.data_structures.ParsedPaper

import scala.collection.mutable.ArrayBuffer

trait Loader {
  
  val formatType: FormatType
  
  def fromDir(dir: File, codec: String = "ISO-8859-1", fileFilter: File => Boolean = _ => true): Iterable[ParsedPaper] = fromFiles(dir.listFiles().filter(fileFilter),codec)

  def fromFiles(files: Iterable[File], codec: String = "ISO-8859-1"): Iterable[ParsedPaper] = {
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

  def fromFilename(filename:String, codec: String = "ISO-8859-1"): Iterable[ParsedPaper] = fromFile(new File(filename),codec)

  def fromFile(file: File, codec: String = "ISO-8859-1"): Iterable[ParsedPaper]

  def fromSeparateFiles(headerFile: File, referencesFile: File, codec: String = "ISO-8859-1"): Option[ParsedPaper]

}


object Loader {
  private val allLoaders = Iterable(LoadGrobid,LoadParsCit,LoadRPP,LoadCora,LoadLocatedCitations,LoadPaperMetadata,LoadCora).map((ldr) => ldr.formatType -> ldr).toMap[FormatType,Loader]
  def apply(formatType: FormatType) = allLoaders(formatType)
  def apply(string: String) = allLoaders(FormatType(string))
}