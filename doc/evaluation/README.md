# Coreference Evaluation #

## Overview ##

The paper coreference algorithm takes as input a collection of paper "mentions," which are ambiguous references to a research paper. These mentions are extracted headers and reference citations from papers. The goal of paper coreference is to group the mentions by the paper to which they refer.

## Evaluation ##

### Pairwise ###

The __Pairwise__ evaluation metric has the following definitions of true positive, false positive and false negative:

```
TP = SUM_{m in Mentions} | PredictedCluster(m) intersect GoldCluster(m) |
FP = SUM_{m in Mentions} | PredictedCluster(m) setdiff GoldCluster(m) |
FN = SUM_{m in Mentions} | GoldCluster(m) setdiff PredictedCluster(m) |
```
 
Precision, recall and F1 are then defined in the standard way:

```
precision = TP / (TP + FP)
recall = TP / (TP + FN)
F1 = 2 * ((precision * recall) / (precision + recall))
```

### MUC ###

The __MUC__ evaluation metric defines precision and recall in the following way:


```
NumMentions(cluster) = the number of mentions in the given cluster
NumTrueEntities(cluster) = given a cluster of mentions, this gives the number of entities referred to by the mentions according to the ground truth labeling.
NumPredictedEntities(cluster) = given a cluster of mentions, this gives the number of entities referred to by the mentions according to the predicted truth labeling.
```

```
precision_numerator = sum_{c in predicted clusters} (NumMentions(c) - NumTrueEntities(c))
precision_denominator = sum_{c in predicted clusters} (NumMentions(c) - 1)
precision = precision_numerator / precision_denominator
```

```
recall_numerator = sum_{c in true clusters} (NumMentions(c) - NumPredictedEntities(c))
recall_denominator = sum_{c in true clusters} (NumMentions(c) - 1)
recall = recall_numerator / recall_denominator
```

### B Cubed ###

The __B Cubed__ evaluation metric defines precision and recall in the following way. See [this blog post](http://brenocon.com/blog/2013/08/probabilistic-interpretation-of-the-b3-coreference-resolution-metric/) for a detailed explanation.

```
precision = (1 / numMentions) sum_{m in Mentions} ( | PredictedCluster(m) intersect GoldCluster(m) | / | PredictedCluster(m) |)
recall = (1 / numMentions) sum_{m in Mentions} ( | PredictedCluster(m) intersect GoldCluster(m) | / | GoldCluster(m) |)
```
