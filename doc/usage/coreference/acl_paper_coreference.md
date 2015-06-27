# ACL Paper Coreference #

This file provides documentation for how to run the coreference algorithm.
 
## Usage ##

The paper coreference experiments on the ACL corpus can be run using the __PaperCoreferenceExperiment__ object. This can be called from the command line:

```
java -Xmx1G -cp target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar org.allenai.scholar.paper_coref.evaluation.PaperCoreferenceExperiment --config=<config-file>
```

Where ```<config-file>``` is a text file containing the command line arguments for the program. Example config files are given in the ```config``` directory of this project. Each config file has the following form:

```
--input=[directory name]
--input-encoding=[iso-8859-1,UTF-8,etc]
--format-type=[Grobid,RPP,ParsCit,etc]
--gold-paper-meta-data=[path-to]/data/metadata
--gold-citation-edges=[path-to]/data/citation-edges
--coref-algorithms=[Baseline,AlphaOnly]
--output=[directory name]
```


## Input Data \& Format Type ##

The input data may be the XML formatted output of ParsCit, Grobid or RPP. Alternatively serialized JSON versions of the data structures, __LocatedCitation__ __ParsedPaper__ and the __PaperMetaData__ may be used as input. 

The format of the input data is specified as a command line argument, ```--format-type```. Each of the possible input types has an associated string for the value of this argument:

| Input Format | --format-type= |
| ------------ | -------------- |
| ParsCit      | ParsCit        |
| Grobid       | Grobid         |
| RPP          | RPP            |
| JSON-LocatedCitation | LocatedCitation |
| JSON-ParsedPaper | ParsedPaper |
| JSON-PaperMetaData | PaperMetaData |

### XML Formats ###

The XML formats assume that each paper's XML data is stored in a single file with the ACL paper id as the base part of the filename. Any additional file extensions are fine. 

### LocatedCitation ###

The input format to the system delivered in January was a JSON file of LocatedCitation objects. This system allows for this same input type. 

### ParsedPaper ###

Input may also come in the form of JSON serialized files of __ParsedPaper__ data structures. Please see the documentation of this data structure for more information on its format.

### PaperMetadata ###

The __PaperMetadata__ data structure in this project follows the same schema as the PaperMetadata data structure in the Meta-Eval project. For simplicity though, this project does not depend on Meta-Eval and so uses a parallel version of the data structure. JSON serialized versions of this data structure may also be used as input. The input must be a directory of files each with a base file name of the ACL paper id. The first PaperMetadata entry in each file must be the header of the paper associated with the file and the remaining entries must be the references/citations. 

### Encoding ###

The file encoding of the input files can be specified. If left out of the config file, the default encoding of "UTF-8" will be used.

## Gold / Evaluation Data ##

The ```data``` directory of this project contains two files, ```metadata``` and ```citation-edges``` which are used to evaluate the coreference algorithm.

## Coreference Algorithms ##

Currently, there are two coreference algorithms implemented, __Baseline__ and __AlphaOnly__. Each work by corefering papers by their titles. __AlphaOnly__ performs an exact string match on the titles of the papers. __Baseline__ performs a relaxed string match. The relaxed string match considers the lowercased, stemmed, non-punctuation tokens of the titles. The __Baseline__ approach performs better than the __AlphaOnly__ approach. 
 
More than one coreference algorithm can be run by providing the command line arguments with a comma separated list. The name of each algorithm is given as the input parameter, i.e. to run both algorithms ```--coref-algorithms=Baseline,AlphaOnly```

## Output ##

The system will write the coreference results to standard out. If the ``--output`` flag is given with an output directory, the system will produce a plain text file ```results.txt``` with the coreference results. Additionally, HTML files with listings of the clusterings will be produced for each algorithm and the gold clustering.
