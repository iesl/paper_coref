# Cora Paper Coreference #

This file provides documentation for how to run the coreference algorithm on the Cora dataset.


## Usage ##

The paper coreference experiments on the Cora dataset can be run using the __CoraPaperCoreferenceExperiment__ object. This can be called from the command line:

```
java -Xmx4G -cp target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar org.allenai.scholar.paper_coref.evaluation.CoraPaperCoreferenceExperiment --config=<config-file>
```

The program uses factorie's command line argument utility. Using the argument ```--config``` allows the user to place command line arguments into the text file given as argument to ```config```.

The command line arguments specify which files to use as input to the program, the format of these files, which coref algorithm to use etc.

Depending on the type of input, ```<config-file>```, a text file, might have the form: 

```
--input=[list of files]
--input-encoding=[iso-8859-1,UTF-8,etc]
--coref-algorithms=[Baseline,AlphaOnly]
--output=[directory name]
```

## Input Data ##

The input data can be downloaded from:  http://people.cs.umass.edu/~mccallum/data/cora-refs.tar.gz . Extract the files from the zipped download. All three files serve as input to the system. These files are: 

```
kibl-labeled
fahl-labeled
utgo-labeled
```

The command line argument is:

```
--input=<path-to>/kibl-labeled,<path-to>/fahl-labeled,<path-to>/utgo-labeled
```

The data has the following format:

```
<NEWREFERENCE>3
aha1987 <author> Kibler, D., & Aha, D. W. </author> <year> (1987). </year> 
<title> Learning representative exemplars of concepts: An initial case study. </title> 
<booktitle> In Proceedings of the Fourth International Workshop on Machine Learning </booktitle> 
<pages> (pp. 24-30). </pages> <address> Irvine, CA: </address> <publisher> Morgan Kaufmann. </publisher>
```

## Coreference Algorithms ##

Currently, there are two coreference algorithms implemented, __Baseline__ and __AlphaOnly__. Each work by corefering papers by their titles. __AlphaOnly__ performs an exact string match on the titles of the papers. __Baseline__ performs a relaxed string match. The relaxed string match considers the lowercased, stemmed, non-punctuation tokens of the titles. The __Baseline__ approach performs better than the __AlphaOnly__ approach.

More than one coreference algorithm can be run by providing the command line arguments with a comma separated list. The name of each algorithm is given as the input parameter, i.e. to run both algorithms ```--coref-algorithms=Baseline,AlphaOnly```

## Output ##

The system will write the coreference results to standard out. If the ``--output`` flag is given with an output directory, the system will produce a plain text file ```results.txt``` with the coreference results. Additionally, HTML files with listings of the clusterings will be produced for each algorithm and the gold clustering.
