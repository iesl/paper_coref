Paper Coreference
=================

Compilation
-----------

To compile this project use Maven. Use the ```package``` command to create a jar with dependencies, which can be used to run all of the components of this project.

```
mvn clean package
```

The jar with dependencies will then be located:

```
target/paper_coref-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Overview
--------

Paper coreference (or citation coreference) is the problem of clustering ambiguous "mentions" of papers into groups such that all of the mentions in the same group refer to the same underlying paper. The ambiguous paper mentions are header and reference information extracted from PDFs using an automatic extraction tool. Performing coreference allows for a citation graph to be created given a set of research papers. 

New Additions (June 2015)
----------------------

- The input to the coreference system can now be the XML output of [Grobid](https://github.com/kermitt2/grobid), [ParsCit](https://github.com/knmnyn/ParsCit), or [RPP](https://github.com/iesl/rpp), as well as JSON serialized versions of project data structures. For more information, see [loading data](doc/usage/loading_data.md).
- Added Cora paper coreference experiment for evaluation.
- Added evaluation results on [ACL data](doc/evaluation/acl_experiment.md) using Grobid and ParsCit extractions and on [the Cora dataset](doc/evaluation/cora_experiment.md).
- Refactored and added package structure 
- Added documentation of code and added user guides.

Usage
------

The [doc](doc/) folder provides an overview of the usage of this project. For running coreference experiments on existing data sets please see: [ACL paper coreference guide](doc/usage/coreference/acl_paper_coreference.md) and  [Cora paper coreference guide](doc/usage/coreference/cora_paper_coreference.md). For information on how to run paper coreference on a new set of data, please see [coreference overview](doc/usage/coreference/coreference_overview.md)

For more information on the general system design see: [system overview](doc/usage/overview.md).

Results
-------

Experimental evaluation and results can be found in [ACL experiment](doc/evaluation/acl_experiment.md) and [Cora experiment](doc/evaluation/cora_experiment.md). A description of the evaluation metrics is available [here](doc/evaluation/README.md). 
