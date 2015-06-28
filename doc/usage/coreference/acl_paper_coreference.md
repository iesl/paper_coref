# ACL Paper Coreference #

This file provides documentation for how to run the coreference algorithm on ACL data.
 
## Usage ##

The paper coreference experiments on the ACL corpus can be run using the __ACLPaperCoreferenceExperiment__ object. This can be called from the command line:

```
java -Xmx4G -cp target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar org.allenai.scholar.paper_coref.evaluation.ACLPaperCoreferenceExperiment --config=<config-file>
```

The program uses factorie's command line argument utility. Using the argument ```--config``` allows the user to place command line arguments into the text file given as argument to ```config```.

The command line arguments specify which files to use as input to the program, the format of these files, which coref algorithm to use, etc.

Depending on the type of input, ```<config-file>```, a text file, might have the form: 

```
--input=[directory name,file of filenames etc]
--input-encoding=[iso-8859-1,UTF-8,etc]
--input-type=["directory","file of filenames", "file"]
--format-type=["Grobid","RPP","ParsCit",etc]
--gold-paper-meta-data=[path-to]/data/metadata
--gold-citation-edges=[path-to]/data/citation-edges
--coref-algorithms=[Baseline,AlphaOnly]
--output=[directory name]
```

or 

```
--headers=[directory name,file of filnames, files etc]
--references=[directory name,file of filenames, files etc]
--input-type=["directory","file of filenames", "file"]
--format-type=["Grobid","RPP","ParsCit",etc]
--gold-paper-meta-data=[path-to]/data/metadata
--gold-citation-edges=[path-to]/data/citation-edges
--coref-algorithms=[Baseline,AlphaOnly]
--output=[directory name]
```

## Input Data ##

Data can either be in the format of one file (containing both header and reference information) per paper or two separate files (one for the header, one for the references) per paper. 

To use the data with one file containing both header and reference information use the command line argument: 

```
--input=<data>
```

To use data with two separate files one for the header, one for the references use the command line arguments:

```
--headers=<data>
--references=<data>
```


The arguments of these command line arguments can be a variety of formats: directories, file of filenames, or files. The type is specified as:

```
--input-type=directory
--input-type=file of filenames
or 
--input-type=file
```

## Format Type ##

The input data may be the XML formatted output of ParsCit, Grobid or RPP. Alternatively serialized JSON versions of the data structures __LocatedCitation__ and the __PaperMetaData__ may be used as input. See [serialization documentation](doc/usage/serialization/serialization.md) for more information.

The format of the input data is specified as a command line argument, ```--format-type```. Each of the possible input types has an associated string for the value of this argument:

| Input Format | --format-type= |
| ------------ | -------------- |
| ParsCit      | ParsCit        |
| Grobid       | Grobid         |
| RPP          | RPP            |
| JSON-LocatedCitation | LocatedCitation |
| JSON-PaperMetaData | PaperMetaData |

### XML Formats ###

The XML formats assume that the ACL paper id is the base part of the filename. Any additional file extensions are fine. For instance, valid filenames would be ```E14-1025.xml, E14-1025.tagged.xml, E14-1025.header.xml.tagged, etc```

### LocatedCitation ###

The input format to the system delivered in January was a JSON file of LocatedCitation objects. This system allows for this same input type. The LocatedCitations may be stored all in a single file or in multiple files. The filenames of the files are not used as the paper ids are serialized in the LocatedCitation structure.

### PaperMetadata ###

The __PaperMetadata__ data structure in this project follows a very similar schema as the PaperMetadata data structure in the Meta-Eval project. This project does not depend on Meta-Eval and so uses a parallel version of the data structure. JSON serialized versions of this data structure may also be used as input. The input must be a directory of files each with a base file name of the ACL paper id. The first PaperMetadata entry in each file must be the header of the paper associated with the file and the remaining entries must be the references/citations.

## Encoding ##

The file encoding of the input files can be specified. The default encoding of "UTF-8" will be used. The following command line argument is used to specify the encoding:

```
--input-encoding=iso-8859-1
--input-encoding-UTF-8
etc
```

## Gold / Evaluation Data ##

The ```data``` directory of this project contains two files, ```metadata``` and ```citation-edges``` which are used to evaluate the coreference algorithm. The command line arguments would be:

```
--gold-paper-meta-data=[path-to]/data/metadata
--gold-citation-edges=[path-to]/data/citation-edges
```

## Coreference Algorithms ##

Currently, there are two coreference algorithms implemented, __Baseline__ and __AlphaOnly__. Each work by corefering papers by their titles. __AlphaOnly__ performs an exact string match on the titles of the papers. __Baseline__ performs a relaxed string match. The relaxed string match considers the lowercased, stemmed, non-punctuation tokens of the titles. The __Baseline__ approach performs better than the __AlphaOnly__ approach. 
 
More than one coreference algorithm can be run by providing the command line arguments with a comma separated list. The name of each algorithm is given as the input parameter, i.e. to run both algorithms ```--coref-algorithms=Baseline,AlphaOnly```

## Output ##

The system will write the coreference results to standard out. If the ``--output`` flag is given with an output directory, the system will produce a plain text file ```results.txt``` with the coreference results.

## Examples ##

The ```config/evaluation/``` directory has several examples of config files. Here are a few examples as well:
 
 ```
 --format-type=Grobid
 --input=<path-to>/data/grobid-acl-processed/full-text
 --input-encoding=iso-8859-1
 --input-type=directory
 --output=evaluation/acl/grobid
 --gold-paper-meta-data=<path-to>/data/metadata
 --gold-citation-edges=<path-to>/data/citation-edges
 --coref-algorithms=Baseline,AlphaOnly
 ```
 
  ```
  --format-type=Grobid
  --input=<path-to>/data/grobid-acl-processed/full-text-filenames.txt
  --input-encoding=iso-8859-1
  --input-type=file of filenames
  --output=evaluation/acl/grobid
  --gold-paper-meta-data=<path-to>/data/metadata
  --gold-citation-edges=<path-to>/data/citation-edges
  --coref-algorithms=Baseline,AlphaOnly
  ```
  
  ```
  --headers=<path to>/data/grobid-acl-processed/headers
  --references=<path to>/data/grobid-acl-processed/references
  --input-encoding=iso-8859-1
  --input-type=directory
  --format-type=Grobid
  --output=evaluation/acl/eval_acl_grobid.txt
  --gold-paper-meta-data=data/metadata
  --gold-citation-edges=data/citation-edges
  --coref-algorithms=Baseline,AlphaOnly
  ```
