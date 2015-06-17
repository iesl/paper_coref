package org.allenai.scholar.paper_coref.load

import java.io.File

import org.allenai.scholar.paper_coref.ParsedPaper

import scala.collection.mutable.ArrayBuffer

trait Loader {
  def fromDir(dir: File, codec: String = "ISO-8859-1", fileFilter: File => Boolean = _ => true): Iterable[ParsedPaper] = dir.listFiles().filter(fileFilter).flatMap(fromFile(_,codec))

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

  def fromFilename(filename:String, codec: String = "ISO-8859-1"): Option[ParsedPaper] = fromFile(new File(filename),codec)

  def fromFile(file: File, codec: String = "ISO-8859-1"): Option[ParsedPaper]

  def fromSeparateFiles(headerFile: File, referencesFile: File, codec: String = "ISO-8859-1"): Option[ParsedPaper]

}
