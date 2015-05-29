#TODO#

##1. Task##

__June 30:__ Port current citation coreference to work with arbitrary extractions, rather than just ParCit. (The prototype citation coreference system that we delivered in January runs only on pre-run header and bibliographic extractions from ParCit, aligned with data from the ACL corpus, which consists of 15,475 entities across 114,654 header and bibliography mentions.)  Evaluate citation coreference on ParCit data using MUC, B3 and pairwise precision and recall metrics.

##2. Timeline##

__June 1.__ Run 3 processors on ACL Corpus

__June 1.__ Draft of universal paper reference data structure format

__June 4.__ Pick ~12 documents for debugging the pdf extraction output, reconsider universal format

__June 12.__ Finalize Universal Format

__June 17.__ First evaluation report

##3. PDF Processors##

- UMass RPP

- Grobid

- ParsCIT

##4. Notes##

* Current plan is to run each processor and provide loader for each output format. Alternatively we could provide support to run each of the different systems from inside the project. 

* In deciding on a data structure format for paper citations, we could use an existing format such as Grobid's format or RPP's format. We want to try not to lose information in converting from the PDF processor's output to the data structure, while keeping the data structure general enough for arbitrarily extracted citations.

* We also want to have a data structure format that is easily serializable and deserializable. Candidates might include JSON based formats which would fit nicely with cubbies and Mongo.  
