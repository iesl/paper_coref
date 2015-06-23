# Coreference Evaluation #

## Overview ##

The paper coreference algorithm takes as input a collection of paper "mentions," which are ambiguous references to a research paper. These mentions are extracted headers and reference citations from papers. The goal of paper coreference is to group the mention by the paper to which they refer.

## Data set ##

The data set used in evaluation is a collection of ACL papers. Each paper in an ACL conference/journal has a unique identifier. A complete list of the papers in the data set is available in ```data/acl_paper_ids.txt```.

### Gold / Ground Truth Data ###

The ACL data set has an associated citation graph. There is a gold labeling of the incoming and outgoing citations of the papers in the data set. This is stored in the ```data/citation-edges``` file. Each line of file contains a pair of paper ids _(from, to)_, such that the paper paper _from_ cites the paper _to_. The first few lines of this file are:

```
C08-3004	A00-1002
D09-1141	A00-1002
D12-1027	A00-1002
E06-1047	A00-1002
H05-1110	A00-1002
```

The ACL data set also has meta data stored about each paper. The file ```data/metadata``` stores the id, title, author, venue, and year for each paper. For instance:

```
id = {D10-1001}
author = {Rush, Alexander M.; Sontag, David; Collins, Michael John; Jaakkola, Tommi}
title = {On Dual Decomposition and Linear Programming Relaxations for Natural Language Processing}
venue = {EMNLP}
year = {2010}
```

## Experiment Setup ##

### PDF Processing ###

First, the PDFs of the ACL data set are processed and converted into a structured representation (in this case XML) using one of the following methods: 
 
 1. PDF => IESL-PDF-To-Text => Research Paper Processor (RPP) => XML Output (RPP's schema)
 2. PDF => Grobid => XML Output (Grobid/TEI schema)
 3. PDF => pdftotext (linux) => ParsCit => XML Output (ParsCit Specific format)
 
### Schema Mapping ###
 
 Next, the schema of the XML outputs of the above systems is mapped into the schema of the paper/citation representation used in the coreference algorithms. This process is done by manually designing a mapping between the two schemas. The paper/citation representation used in the coreference algorithms is defined in ```doc/schema_mapping/PaperCoreferenceDatastrctures.md```. The schema mapping techniques used are given in ```doc/schema_mapping```.
 
 A key aspect of the schema mapping in the clustering evaluation is that each PDF is associated with an ACL paper id. For a given paper, we extract a single header mention (representing the paper with the given id) and one or reference citation mentions. Together these make up a __ParsedPaper__ representation used in the coreference system. Also note that if no header is extracted for a given PDF, the references mentioned in that paper are discarded. That is we __only__ work with papers for which a header is extracted. The mentions maintain the provenance of the id of the PDF they were extracted from. That is the header mentions have the id of the paper they refer to and the reference citations have the id of the paper they were cited in. 
 
### Alignment ###

We use the ground truth citation graph and metadata to create a ground truth clustering used to evaluate the output of the coreference algorithm. The ground truth clustering considers each paper entry in the metadata file and outgoing edge in the citation graph as a mention. The ground truth clustering assigns each mention to a cluster representing the paper to which the mention refers (this is simply the paper id listed in either the metadata or citation edge file). And so there is one cluster per paper id.
 
In order to evaluate the coreference output, each extracted mention must be aligned with a mention the ground truth data so that it can have a gold label. The extracted mentions are grouped by their provenance information-- the paper id of the PDF from which they were extracted (this is the __ParsedPaper__ representation). Each group has a header mention and a group of reference mentions. The gold cluster label of the header mention is simply its provenance paper id. To determine the gold cluster label of the reference mentions, the gold citation edges are used to get a list of the papers which should be appear as reference for the particular paper. The extracted mentions are greedily aligned by the edit distance between the extracted mentions titles and the titles (drawn from the metadata) of the papers given by the citation edges. All mentions, which do not receive a gold label in this alignment process are discarded. 
 
### Coreference ###
 
The remaining mentions are then entered into the coreference algorithm. The coreference algorithm produces a clustering of the mentions (without using the gold labels of course). This predicted clustering of mentions can then be evaluated against the gold labeling determined in the previous step.

### Evaluation ###
 
The Pairwise, MUC and B3 metrics are used to evaluate the clustering.  TODO: Provide clear definition of each of these.

### Results ###


| Coref System | Processor   | Pairwise Precision  |  Pairwise Recall | Pairwise F1     | MUC Precision  |  MUC Recall | MUC F1     | B3 Precision  |  B3 Recall | B3 F1     |
| ----------- | ----------- | ------------------- | ---------------- | --------------- | -------------- | ----------- | ---------- | ------------- | ---------- | --------- |
??? | Ai2 Parscit | 97.711 | 63.600 | 77.049 | 98.230 | 81.282 | 88.956 | 97.903 | 68.101 | 80.326 |
"Baseline" | pdftotext + Parscit | 97.256 | 58.231 | 72.846 | 97.715 | 76.307 | 85.694 | 97.659 | 62.050 | 75.885 |
"AlphaOnly" | pdftotext + Parscit | 97.749 | 53.003 | 68.735 | 97.637 | 66.816 | 79.338 | 97.920 | 52.373 | 68.245 |
"Baseline" | Grobid | 98.756 | 73.653 | 84.377 | 99.142 | 84.938 | 91.492 | 98.718 | 74.537 | 84.940 |
"AlphaOnly" | Grobid | 98.908 | 69.017 | 81.302 | 99.172 | 82.271 | 89.935 | 98.796 | 70.126 | 82.028 |


 
