# Paper Coreference Data Structures #

## RawCitation ##

A __RawCitation__ is a representation of a paper. It could either be the header of a paper or an entry in the reference section of the paper. Its schema is similar to the PaperMetaData data structure in __Meta-Eval__. The fields of a __RawCitation__ are:

- rawTitle:String - _The title of the paper as it was extracted from the PDF_
- rawAuthors:List[String] - _A list of authors. The author names are not necessarily structured, e.g. first and last names are not necessarily annotated or differentiated_
- date:String - _The date (often just the year) in the way it was extracted from the PDF, e.g. without formatting_
- venue: String - _The venue as it was extracted from the PDF_

## LocatedCitation ##

A __LocatedCitation__ represents a "mention" of a paper. It is a __RawCitation__ that also contains either the unique id of the paper represented by the "mention" or the unique id of the paper which contains this citation in its reference.

- rawCitation:RawCitation - _The citation of the mention_
- citingPaperId:Option[String] - _The unique id of the paper which contains this citation in its references_
- paperId:Option[String] - _The unique id of the paper represented by the mention_

## ParsedPaper ##

A __ParsedPaper__ is representation of a scientific paper, it is represented by a __LocatedCitation__ of the paper itself and a collection of __LocatedCitations__ for the papers cited by the paper. The fields of a __ParsedPaper__:

- self:LocatedCitation - _The reference representing the paper itself_
- bib:Iterable[LocatedCitation] - _The papers referenced in this paper_

## PaperMention ##

The coreference algorithms work on __PaperMention__ data structures. This data structure stores the information present in the citation data structures along with an id for the mention and gold label used for evaluation.


- id: String - _a unique id for the mention_
- authors: Set[String] - _the authors of the paper_
- title: String - _the title of the paper_
- venue: String - _the venue the paper appears in_
- date: String - _the date published_
- trueLabel: String - _the id of the gold clustering_
- isPaper: Boolean - _true iff the paper was a header extraction_
- goldData: PaperMention - _the corresponding gold mention data_

## PaperMetadata and PaperMetadataWithId #

To emulate the data structures used in Meta-Eval this project has a parallel version of the __PaperMetadata__. This data structure has the following fields:


- title: String
- venue: String
- year: Int
- authors: List[String]

__PaperMetadataWithId__ also includes: 

- id: String