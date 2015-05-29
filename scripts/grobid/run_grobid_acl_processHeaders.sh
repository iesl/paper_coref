#!/bin/sh

task=processHeader
outputDir=/iesl/canvas/nmonath/grant_work/ai2/data/acl-processed/headers/

mkdir -p $outputDir

sh run_grobid_acl.sh $task $outputDir
