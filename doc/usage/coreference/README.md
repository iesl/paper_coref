ACL Paper Coreference Problem Overview
----------------

We want to produce a citation graph from extracted title and bibliographic matter of PDFs of scientific papers. In practice this means we want to cluster citations from the bibliographies of papers together with citations from the titles of papers. Once we have these clusters we can link citations found in papers to the papers they cite by keeping track of the original provenance of each citation.

In order to evaluate the system, we need to have a gold set of data, but this is not trivially available as the citations we are working with were extracted by a particular tool, and we want to have annotations that are robust across tools. To solve this the system also has a simple aligner that will generate gold data from a citation graph, metadata from that citation graph, and the results of extracting citations from the relevant dataset using an arbitrary extraction tool. Concretely, the aligner looks at all the citations that appeared in a given paper, looks at the names of the paper that that paper is known to have cited, and aligns the titles greedily in terms of those that are closest in edit distance (with a threshold to ensure that very poor matches aren't considered.)

Approach
--------

We tested a simple possible approach to determine whether it made sense to invest further effort in coreference or in improving the quality of citation extraction directly. To that end, the coreference system adopts a hashing approach. It tokenizes the paper title, removes all punctuation, normalizes whitespace, and stems the words. All these nlp services are provided by [FACTORIE](http://factorie.cs.umass.edu). The string resulting from this process is considered a hash of the title. Each extracted citation is clustered by this hash, which forms the coreference hypothesis that we evaluate.

Additional Evaluation
=====================

We also evaluate the coreference algorithms on the Cora paper coreference dataset, which has hand annotated gold labels for evaluation.