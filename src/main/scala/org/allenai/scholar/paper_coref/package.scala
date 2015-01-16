package org.allenai.scholar

/**
 * @author John Sullivan
 */
package object paper_coref {

  case class Percent(num:Int, denom:Int) {
    lazy val value = (num.toDouble / denom)*100
    override def toString = s"$num of $denom ($value %)"
  }

  implicit class IterableExtras[A](coll:Iterable[A]) {
    def percentWhere(pred:A => Boolean):Percent = {
      val denom = coll.size
      val num = coll.count(pred)
      Percent(num, denom)
    }
  }

}
