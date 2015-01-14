package org.allenai.scholar

/**
 * @author John Sullivan
 */
package object paper_coref {

  implicit class IterableExtras[A](coll:Iterable[A]) {
    def percentWhere(pred:A => Boolean):(Int, Int, Double) = {
      val denom = coll.size
      val num = coll.count(pred)
      (num, denom, (num.toDouble/denom)*100)
    }
  }

}
