# Coreference #

## Running Experiments ##
See:
- [ACL](acl_paper_coreference.md)
- [Cora](cora_paper_coreference.md)

## PaperCoref Interface ##

Paper Coreference Algorithms in this project implement the following interface:


```Scala
  val name: String
  def performCoref(mentions:Iterable[PaperMention]):Iterable[Iterable[PaperMention]]
```

The ```name``` field gives the name of the coreference algorithm.

The ```performCoref``` method clusters the inputted mentions. The output is formatted such that each element in the iterable represents a cluster of paper mentions. Each cluster is also represented as an iterable.

## PaperCorefExperiment ##

The class ```PaperCorefExperiment``` can be used to run a coreference experiment. The constructor is:


```Scala
PaperCoreferenceExperiment(val mentions: Iterable[PaperMention], val corefs: Iterable[PaperCoref]) 
```

A set of mentions and coref algorithms are inputted into the experiment. Several other constructors are included. Each constructor is documented in the [source code](https://github.com/iesl/citation_coref/blob/develop/src/main/scala/org/allenai/scholar/paper_coref/evaluation/PaperCoreferenceExperiment.scala)

Use:

```Scala
def run()
```

To run and evaluate the coreference algorithm. The method returns the coreference results of each algorithm as a formatted string.

The resulting clustering of the coreference algorithms can be accessed by using the following methods in PaperCorefExperiment.

```Scala
def predictedClusteringResults: Map[String,Iterable[Iterable[PaperMention]]
def predictedClustering(corefAlgName: String): Iterable[Iterable[PaperMention]]
```


