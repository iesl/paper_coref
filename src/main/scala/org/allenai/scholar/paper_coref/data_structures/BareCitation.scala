package org.allenai.scholar.paper_coref.data_structures

import java.io._

import cc.factorie._

/**
 * A simple representation of a citation link given two paper ids.  
 * @author John Sullivan
 */
case class BareCitation(from:String, to:String)

/**
 * A few convenience methods for BareCitation
 */
object BareCitation {

  def fromInputStream(is:InputStream):Iterable[BareCitation] =
    new BufferedReader(new InputStreamReader(is)).toIterator.map{ line =>
      val Array(from, to) = line split """\s+"""
      BareCitation(from, to)
    }.toIterable

  def fromFile(filename:String):Iterable[BareCitation] = fromInputStream(new FileInputStream(filename))
}
