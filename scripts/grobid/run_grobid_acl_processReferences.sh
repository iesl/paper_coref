#!/bin/sh

task=processReferences
outputDir=/iesl/canvas/nmonath/grant_work/ai2/data/acl-processed/references/

mkdir -p $outputDir

sh run_grobid_acl.sh $task $outputDir
