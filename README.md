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

Usage
------

The [doc](doc/) folder provides an overview of the usage of this project. For running coreference experiments on existing data sets please see: [ACL paper coreference guide](doc/usage/coreference/acl_paper_coreference.md) and  [Cora paper coreference guide](doc/usage/coreference/cora_paper_coreference.md). For information on how to run paper coreference on a new set of data, please see [coreference overview](doc/usage/coreference/coreference_overview.md)

For more information on the general system design see: [system overview](doc/usage/overview.md).

Results
-------

Experimental evaluation and results can be found in [ACL experiment](doc/evaluation/acl_experiment.md) and [Cora experiment](doc/evaluation/cora_experiment.md). A description of the evaluation metrics is available [here](doc/evaluation/README.md). 
