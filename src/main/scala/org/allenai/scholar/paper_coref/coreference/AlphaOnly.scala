package org.allenai.scholar.paper_coref.coreference

object AlphaOnly extends HashingCoref {

  override val name = "AlphaOnly"

  def titleHash(title: String) = title.replaceAll("""\s+""", " ").toLowerCase.replaceAll("""^[\w]""","")
}
