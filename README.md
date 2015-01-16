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

This is over a dataset with 114,654 mentions (citations/titles) across 15,475 entities (papers). Using the same extraction technique but labelling empty title string as singletons gives:


| Metric   | Precision | Recall | F1     |
| -------- | --------- | ------ | ------ |
| Pairwise | 97.711    | 63.079 | 76.666 |
| MUC      | 98.230    | 77.608 | 86.710 |
| B^3      | 98.028    | 65.146 | 78.274 |

This is over a dataset with 121,938 mentions and 18,064 entities.

(For an intuition on these metrics you might want to look [here](http://brenocon.com/blog/2013/08/probabilistic-interpretation-of-the-b3-coreference-resolution-metric/).)


Citation Metrics
----------------

Separate from the result of coreference, we want to be able to evaluate the quality of the extracted citations, since the quality of the citation graph depends heavily on this. To this end, there is also code to evaluate the quality of the extracted paper and bibliography citations against a known citation graph. At the moment, the following metrics are calculated

| Name | Explanation |
| ---- | ----------- |
| Paper citations | The proportion of the citation records that belong to paper titles (as opposed to bibliographies) |
| Bibliography citations | The proportion of the citation records that belong to bibliographies (as opposed to paper titles) |
| Missing titles (empty string) | The proportion of records where no title was extracted |
| Missing authors (empty list or list of empty strings) | The proportion of records where no authors were extracted |
| Paper cits with missing titles | The proportion of paper titles where no title was extracted |
| Bib cits with missing titles | The proportion of bibliography entries where no title was extracted |
| Paper cits with missing authors | The proportion of papers titles where no authors were extracted |
| Bib cits with missing authors | The proportion of bibliography entries where no authors were extracted |
| Papers with exactly one paper cit | The proportion of papers that had only one citation record that was marked as the paper title (should always be 100%) |
| Papers with > 1 paper cit | The proportion of papers that had more than one citation record that was marked as the paper title (should always be 0%) |
| Papers with 0 paper cits | the proportion of papers that had no citation records marked as a paper title (should always be 0%) |
| Scraped papers aligned to gold (of scraped) | The proportion of automatically extracted papers that could be aligned to entries in the gold dataset |
| Scraped papers aligned to gold (of gold) | The proportion of gold entries that could be aligned to automatically extracted papers |
| Aligned papers title exact match | The proportion of aligned papers whose titles exactly matched the corresponding gold entry's title |
| Aligned papers title downcase trim match | The proportion of aligned papers whose titles matched the corresponding gold entry's title after lowercasing and normalizing whitespace |
| Aligned papers title stemmed match | The proportion of aligned papers whose titles matched the corresponding gold entry's title after tokenizing, lowercasing, stemming words, and removing punctuation |
| Aligned papers without empty titles title exact match | The proportion of aligned papers with non-empty titles whose titles exactly matched the corresponding gold entry's title |
| Aligned papers without empty titles title downcase trim match | The proportion of aligned papers with non-empty titles whose titles matched the corresponding gold entry's title after lowercasing and normalizing whitespace |
| Aligned papers without empty titles title stemmed match | The proportion of aligned papers with non-empty titles whose titles matched the corresponding gold entry's title after tokenizing, lowercasing, stemming words, and removing punctuation |
| Aligned Papers with empty predicted bibs | The proportion of aligned papers where we did not find any bibliographic entries |
| Aligned Papers with empty gold bibs | The proportion of gold papers with no bibliographic entries |
| Aligned papers with empty gold and predicted bibs | The proportion of papers where both extracted papers and gold papers had no bibliographic entries |
| Aligned papers whose predicted bibs all have empty titles | The proportion of papers where all of the extracted bibliographic entries have no titles |
| After alignment, empty alignments | The proportion of papers where we were not able to align any extracted bibliographic entries to gold bibliographic entries |
| Aligned bib citations title exact match | The proportion of aligned bibliographic entries whose titles exactly matched the corresponding gold entry's title |
Aligned bib citations title downcase trim match | The proportion of aligned bibliographic entries whose titles matched the corresponding gold entry's title after lowercasing and normalizing whitespace |
Aligned bib citations title stemmed match | The proportion of aligned bibliographic entries whose titles matched the corresponding gold entry's title after tokenizing, lowercasing, stemming words, and removing punctuation |
Nonempty Aligned bib citations title exact match | The proportion of aligned bibliographic entries with non-empty titles whose titles exactly matched the corresponding gold entry's title |
Nonempty Aligned bib citations title downcase trim match | The proportion of aligned bibliographic entries with non-empty titles whose titles matched the corresponding gold entry's title after lowercasing and normalizing whitespace |
Nonempty Aligned bib citations title stemmed match | The proportion of aligned bibliographic entries with non-empty titles whose titles matched the corresponding gold entry's title after tokenizing, lowercasing, stemming words, and removing punctuation |
