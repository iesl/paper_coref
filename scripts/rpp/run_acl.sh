#!/bin/bash

dataFilesFile=$1
#/iesl/canvas/nmonath/grant_work/ai2/data/data_filepaths/iesl-pdf-to-text-acl-processed-filepaths.txt
citation_coref_root=/iesl/canvas/nmonath/grant_work/ai2/citation_coref/
rpp_root=/iesl/canvas/nmonath/grant_work/ai2/rpp/
lexicons=file://$rpp_root/cc/factorie/app/nlp/lexicon
outputFile=/iesl/canvas/nmonath/grant_work/ai2/data/rpp-processed-output


sh $citation_coref_root/scripts/rpp/batchrun.sh $rpp_root $lexicons $dataFilesFile $outputFile
