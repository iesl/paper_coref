package org.allenai.scholar.paper_coref

import cc.factorie._
import java.io.{BufferedReader, FileReader}

/**
 * @author John Sullivan
 */
case class BareCitation(from:String, to:String)

object BareCitation {
  def fromFile(filename:String):Iterable[BareCitation] = new BufferedReader(new FileReader(filename))
    .toIterator.map{ line =>
    val Array(from, to) = line split """\s+"""
    BareCitation(from, to)
  }.toIterable
}
