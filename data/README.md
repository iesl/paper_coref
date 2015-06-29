# Paper Coreference Data #

This directory contains data files used for system evaluation. 

The data files are publically available from: https://github.com/allenai/meta-eval Download the metadata and citation-edges files and place them in this directory.

## metadata ##

A file containing the id, authors, title, venue and year of nearly all of the papers in the collection. The following paper ids appear in the __citation\_edges__ file, but not in this __metadata__ file:

```
C98-1012
C98-1062
C98-1077
C98-1112
C98-2201
C98-2214
C98-2233
P98-2133
```

## citation\_edges ##

Each line of this file indicates an edge in the citation graph of the papers in the acl dataset. The lines are tab separated paper ids. 


## acl\_paper\_ids.txt ##

A text file of all of the paper ids in the dataset. This was created by collecting all of the unique ids in the __metadata__ and __citation\_edges__ files. There are 20997 unique ids.

```
cat <( cut -f 1 citation-edges) <( cut -f 2 citation-edges) <(grep "id = {" metadata | sed "s/id = {//g" | sed "s/}//g") | sort -u >> acl_paper_ids.txt
```
