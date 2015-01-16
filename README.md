Paper Hash
==========

A super simple mock-up of a system to perform paper coreference which allows the construction of a citation graph.

Problem Overview
----------------

We want to produce a citation graph from extracted title and bibliographic matter of PDFs of scientific papers. In practice this means we want to cluster citations from the bibliographies of papers together with citations from the titles of papers. Once we have these clusters we can links citations found in papers to the papers they sight by keeping track of the original provenance of each citation.

In order to evaluate the system, we need to have a gold set of data, but this is not trivially available as the citations we are working with were extracted by a particular tool, and we want to have annotations that are robust across tools. To solve this the system also has a simple aligner that will generate gold data from a citation graph, metadata from that citation graph, and the results of extracting citations from the relevant dataset using an arbitrary extraction tool. Concretely, the aligner looks at all the citations that appeared in a given paper, looks at the names of the paper that that paper is known to have cited, and aligns the titles greedily in terms of those that are closest in edit distance (with a threshold to ensure that very poor matches aren't considered.)

Approach
--------

For this early proof-of-concept, we wanted to test the simplest possible approach to determine whether it made sense to invest further effort in coreference or in improving the quality of citation extraction directly. To that end, the coreference system adopts a hashing approach. It tokenizes the paper title, removes all punctuation, normalizes whitespace, and stems the words. All these nlp services are provided by [FACTORIE](http://factorie.cs.umass.edu). The string resulting from this process is considered a hash of the title. Each extracted citation is clustered by this hash, which forms the coreference hypothesis that we evaluate.

Performance
-----------

The system performs quite well on the labeled ACL data set with alignment performed. Using Parcit's extraction on the labeled ACL data after alignment of the citations and with empty titles removed the simple string hashing technique achieves the following results:

| Metric   | Precision | Recall | F1     |
| -------- | --------- | ------ | ------ |
| Pairwise | 97.711    | 63.600 | 77.049 |
| MUC      | 98.230    | 81.282 | 88.956 |
| B^3      | 97.903    | 68.101 | 80.326 |

This is over a dataset with 114,654 mentions (citations/titles) across 15,475 entities (papers). (For an intuition on these metrics you might want to look [here](http://brenocon.com/blog/2013/08/probabilistic-interpretation-of-the-b3-coreference-resolution-metric/).)
