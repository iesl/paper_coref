# Citation Metrics #

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


## Usage ##

To generate these statistics, use the ```CitationMetrics``` object. For example,

```
java -Xmx8G -cp target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar org.allenai.scholar.paper_coref.evaluation.CitationMetrics --config=<config-file>
```

where the config file has the same format as in the [acl experiment](acl_paper_coreference.md), except without the ```output``` argument.
