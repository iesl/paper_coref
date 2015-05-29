#!/bin/sh

task=processFullText
outputDir=/iesl/canvas/nmonath/grant_work/ai2/data/acl-processed/full-text/

mkdir -p $outputDir

sh run_grobid_acl.sh $task $outputDir
