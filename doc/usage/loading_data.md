# Loading Data #


The trait __Loader__ defines methods for loading data. 

```Scala
val formatType: FormatType
def fromDir(dir: File, codec: String = "UTF-8", fileFilter: File => Boolean = _ => true): Iterable[ParsedPaper]
def fromFiles(files: Iterable[File], codec: String = "UTF-8"): Iterable[ParsedPaper]
def fromFilename(filename:String, codec: String = "UTF-8"): Iterable[ParsedPaper]
def fromFile(file: File, codec: String = "UTF-8"): Iterable[ParsedPaper]
def fromSeparateHeaderAndReferenceFile(headerFile: File, referencesFile: File, codec: String = "UTF-8")
def fromSeparateFiles(headersAndReferences: Iterable[(File,File)], codec: String = "UTF-8"): Iterable[ParsedPaper]
```

The trait __XMLLoader__ further specifies the interface for cases in which the input data is in XML format. 
 
 ```Scala
 def loadHeader(xml: Elem): Option[RawCitation]
 def loadReferences(xml: Elem):Iterable[RawCitation]
 def loadReference(xml: NodeSeq): Option[RawCitation]
 ```
 
 The trait __MentionLoader__ specifies the following interface. Only the Cora loader implements this interface:
 
 ```Scala
 def loadMentionsFromFile(file: File, codec: String) : Iterable[PaperMention]
 ```
 
Each __Loader__ has an associated __FormatType__ which specifies how the data is to be loaded. Below is a list of the available __Loaders__ and __FormatTypes__.

| FormatType (String for command line arguments) | FormatType (object name) | Loader | 
| ---------------------------------------------- | ------------------------ | ------ |
|  Grobid | GrobidFormat | LoadGrobid |
| ParsCit | ParsCitFormat | LoadParsCit | 
| RPP | RPPFormat | LoadRPP |
| Cora | CoraFormat| LoadCora |
| LocatedCitation | LocatedCitationFormat | LoadLocatedCitations |
| PaperMetadata | PaperMetadataFormat | LoadPaperMetadata |


For more information on how __LoadGrobid__, __LoadParsCit__ and __LoadRPP__ convert the respective XML schemas to the paper coreference data structures. Please see [schema mapping documentation](../schema_mapping).
 
For more information on the __RawCitation__, __ParsedPaper__ and other data structures see [coreference data structures](data_structures.md).

