package org.allenai.scholar.paper_coref.evaluation

import org.allenai.scholar.paper_coref.data_structures.PaperMention


object HTMLReport {
  
  
  def generateHTML(clustering:Iterable[Iterable[PaperMention]], name: Option[String] = None) = {
    val sb = new StringBuffer(1000)
    
    sb append "<html>\n<body><h1>"
    sb append name.getOrElse("Paper Clustering")
    sb append "</h1>"
    
    clustering.zipWithIndex.foreach{
      case (cluster,idx) =>
        sb append s"<h2> Cluster #$idx </h2>"
        cluster.foreach{
          mention =>
            sb append s"<h4> Gold Id: ${mention.trueLabel}</h4>"
            sb append s"<ul> <li> <b>Title:</b> ${mention.title} </li>\n <li> <b>Authors:</b> ${mention.authors.mkString(" || ")} </li>\n <li> <b>Venue:</b> ${mention.venue}</li>\n <li> <b>Date:</b> ${mention.date}\n</li> </ul>"
        }
        val thisClusterGoldIds = cluster.map(_.trueLabel).toSet
        sb append s"<ul> <li> <i> There were ${clustering.filterNot(_ == cluster).flatten.count((p) => thisClusterGoldIds.contains(p.trueLabel))} other mentions with one of the gold ids that appear in this cluster. </i> </li> "
        sb append s"<li> <i> These other mentions appear in ${clustering.filterNot(_ == cluster).count((i) => i.exists((p) => thisClusterGoldIds.contains(p.trueLabel)))} other different clusters.</i></li></ul>"
    }

    sb append "\n</body>\n</html>"
    sb.toString
  }

}
