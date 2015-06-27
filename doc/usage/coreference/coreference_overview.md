# Coreference #


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

A set of mentions and coref algorithms are inputted into the experiment. Use the ```.run()``` method to run and evaluate the coreference algorithm. 
