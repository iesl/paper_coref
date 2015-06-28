# Coreference Evaluation #

## Overview ##

The paper coreference algorithm takes as input a collection of paper "mentions," which are ambiguous references to a research paper. These mentions are extracted headers and reference citations from papers. The goal of paper coreference is to group the mention by the paper to which they refer.

## Evaluation ##

### Pairwise ###

The __Pairwise__ evaluation metric has the following definitions of true positive, false positive and false negative:

```
TP = SUM_{m in Mentions} | PredictedCluster(m) intersect GoldCLuster(m) |
```

```
FP = SUM_{m in Mentions} | PredictedCluster(m) setdiff GoldCLuster(m) |
```

```
FN = SUM_{m in Mentions} | GoldCLuster(m) setdiff PredictedCluster(m) |
```
 
Precision, recall and F1 are then defined in the standard way:

```
precision = TP / (TP + FP)
```

```
recall = TP / (TP + FN)
```

```
F1 = 2 * ((precision * recall) / (precision + recall))
```

### MUC ###


TODO

### B Cubed ###

TODO
