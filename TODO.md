#TODO#

##1. Task##

__June 30:__ Port current citation coreference to work with arbitrary extractions, rather than just ParCit. (The prototype citation coreference system that we delivered in January runs only on pre-run header and bibliographic extractions from ParCit, aligned with data from the ACL corpus, which consists of 15,475 entities across 114,654 header and bibliography mentions.)  Evaluate citation coreference on ParCit data using MUC, B3 and pairwise precision and recall metrics.

##2. Timeline##

__June 1.__ Run 3 processors on ACL Corpus (see below)

- Grobid. _Complete._ Few error cases outstanding.
  - Error for the _full text extraction_ is: 
  ```
  warning: missing tokens, cannot apply pattern
  warning: Cannot apply patterns, most likely wrong input data
  ```
  - There are some corrupt files that cause this (e.g. html & postscript). See __pdftotext__ below for more info on these.
  - The other PDFs causing this error (it's not really a warning it seems)  do not have any obvious sign of problems. (all of the files are located here: ```/iesl/canvas/nmonath/grant_work/ai2/data/acl-grobid-full-text-errors```)
- ParsCit. _Complete_. Notes: Input format is plain text. For initial run used Linux utility pdftotext to convert pdfs to text.
- RPP. _In progress_. Running iesl-pdf-to-text on ACL. Issues running RPP still being sorted out.

__June 1.__ Draft of universal paper reference data structure format

- _In progress_. Writing loaders of the XML output of each of the processors. Rather than defining a new data structure format, I am for the time being writing loaders, which convert the XML output into the ```RawCitation```,```LocatedCitation```, and ```ParsedPaper``` data structures that already exist in this project. See ```LoadGrobid```.

__June 4.__ Pick ~12 documents for debugging the pdf extraction output, reconsider universal format

- A set of documents for debugging purposes are available here: 
```
/iesl/canvas/nmonath/grant_work/ai2/data/acl-debug-set
```
- RE: universal format -- writing loaders to convert xml output to Jack's existing data structures.

__June 8.__ Formalize the schema mapping problem. Provide examples. Get scores for at least 1 of the processors. 

- Formalization still needs work
- See below for scores for ParsCit & Grobid

__June 12.__ Finalize Universal Format

__June 17.__ First evaluation report

## 3. Processors ##

### 3.1 PDF to Text or SVG ###

- __pdftotext (linux utility).__ Ran this on the ACL corpus. Found error cases on the following documents: L08-1004,L08-1005,L08-1006,L08-1007,L08-1008,L08-1009,P03-1024,P03-2004,W03-1613,W04-1102,W96-0007. These documents are either html pages saying that the requested document could not be processed, postscript documents, or pdf documents which the utility says is corrupted. 
  - I have the __pdftotext__ processed corpus here: ```/iesl/canvas/nmonath/grant_work/ai2/data/pdftotext-acl-processed```
- __iesl-pdf-to-text.__ Ran this on the ACL corpus. It is quite slow since it doesn't have a batch mode. While I originally ran this in a serial fashion, there is now some code set up to run in a distributed way. 
  - I have the __iesl-pdf-to-text__ processed here: ```/iesl/canvas/nmonath/grant_work/ai2/data/iesl-pdf-to-text-acl-processed```

### 3.2 Document Processors ###

- __Grobid.__ Ran the three modes of interest on the ACL corpus, full text processing, header processing, and reference section processing. There were some error cases other than the ones that occurred with __pdftotext__. Additionally, the encoding of the compute nodes is ```iso-8859-1```; I would like to investigate if we could instead use ```UTF-8```.
- __ParsCit.__ After a slightly complicated set up process, ParsCit runs on blake. See the _doc_ directory for more info on the installation. Currently using __pdftotext__ to generate the plain text input to __ParsCit__. However, I need to talk to our folks here about using __iesl-pdf-to-text__ to generate input or the AI2 folks about what they use.
- __RPP.__ I've run into issues using this. I think I have not set it up properly. I am trying currently to resolve these. 

##4. Coreference Results##

- Initial run with current, not-so sophisticated schema mapping, gives: 


| Coref System | Processor   | Pairwise Precision  |  Pairwise Recall | Pairwise F1     | MUC Precision  |  MUC Recall | MUC F1     | B3 Precision  |  B3 Recall | B3 F1     |
| ----------- | ----------- | ------------------- | ---------------- | --------------- | -------------- | ----------- | ---------- | ------------- | ---------- | --------- |
??? | Ai2 Parscit | 97.711 | 63.600 | 77.049 | 98.230 | 81.282 | 88.956 | 97.903 | 68.101 | 80.326 |
"Baseline" | pdftotext + Parscit | 97.256 | 58.231 | 72.846 | 97.715 | 76.307 | 85.694 | 97.659 | 62.050 | 75.885 |
"AlphaOnly" | pdftotext + Parscit | 97.749 | 53.003 | 68.735 | 97.637 | 66.816 | 79.338 | 97.920 | 52.373 | 68.245 |
"Baseline" | Grobid | 98.085 | 72.477 | 83.359 | 97.813 | 83.665 | 90.188 | 97.566 | 73.349 | 83.742 |
"AlphaOnly" | Grobid | | 98.255 | 68.044 | 80.405 | 97.884 | 81.124 | 88.719 | 97.710 | 69.133 | 80.974


##5. Notes##

* Current plan is to run each processor and provide loader for each output format. Alternatively we could provide support to run each of the different systems from inside the project. 

* In deciding on a data structure format for paper citations, we could use an existing format such as Grobid's format or RPP's format. We want to try not to lose information in converting from the PDF processor's output to the data structure, while keeping the data structure general enough for arbitrarily extracted citations.

* We also want to have a data structure format that is easily serializable and deserializable. Candidates might include JSON based formats which would fit nicely with cubbies and Mongo.  

##6. General TODO ##

- Investigate the malformed XML errors present in the output of Grobid and Parscit
- Debugging RPP
- Documentation
- JSON Serialization
- Results using Rexa MetaTagger?
- Continue to document the schema mapping
- More details on the extractions that Jack had been using. 
- Load metadata from JSON
- Investigate any errors remaining from PDF processing
- Test the parallelized iesl-pdf-to-text
