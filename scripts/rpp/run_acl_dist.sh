#!/bin/bash


CITATION_COREF_ROOT=/iesl/canvas/nmonath/grant_work/ai2/citation_coref
N=100
filenameFile=/iesl/canvas/nmonath/grant_work/ai2/data/data_filepaths/iesl-pdf-to-text-acl-processed-filepaths.txt
rm -rf file-splits
mkdir file-splits
mkdir qsub-outputs
splitFilename=file-splits/split


sh $CITATION_COREF_ROOT/scripts/rpp/split_data_files_list.sh $filenameFile $N $splitFilename

counter=0
while [ $counter -lt $N ]; do
    echo sh $CITATION_COREF_ROOT/scripts/rpp/run_acl.sh "$splitFilename-$counter.txt" | qsub -cwd -pe blake 1 -S /bin/sh -N split-${counter} -o qsub-outputs/split-${counter}.out -e qsub-outputs/split-${counter}.err
    counter=$(( counter + 1 ))
done
