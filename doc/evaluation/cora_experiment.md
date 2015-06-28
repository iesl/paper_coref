# Cora #

## Data set ##


The Cora dataset can be obtained from here: http://people.cs.umass.edu/~mccallum/data/cora-refs.tar.gz . It consists of 1879 paper mentions referring to 192 papers. The mentions were human annotated; this annotation is used for evaluation. 


## Results ## 


| Coref System | Pairwise Precision  |  Pairwise Recall | Pairwise F1     | MUC Precision  |  MUC Recall | MUC F1     | B3 Precision  |  B3 Recall | B3 F1     |
| -----------  | ------------------- | ---------------- | --------------- | -------------- | ----------- | ---------- | ------------- | ---------- | --------- |
| Baseline | 83.943 | 74.995 | 79.217 | 97.589 | 91.168 | 94.269 | 88.626 | 76.822 | 82.303 |
| AlphaOnly | 86.623 | 50.928 | 64.144 | 97.247 | 83.758 | 90.000 | 90.264 | 57.779 | 70.457 |
