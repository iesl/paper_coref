package org.allenai.scholar.paper_coref

import cc.factorie._
import java.io._

/**
 * @author John Sullivan
 */
case class BareCitation(from:String, to:String)

object BareCitation {

  def fromInputStream(is:InputStream):Iterable[BareCitation] =
    new BufferedReader(new InputStreamReader(is)).toIterator.map{ line =>
      val Array(from, to) = line split """\s+"""
      BareCitation(from, to)
    }.toIterable

  def fromFile(filename:String):Iterable[BareCitation] = fromInputStream(new FileInputStream(filename))
}
