#!/bin/sh

# Usage: ./run_grobid_acl task outputDir

memory=1G # assume 1G, all we need for grobid
task=$1
GROBID_ROOT=/iesl/canvas/nmonath/grant_work/ai2/grobid/
inputDir=/iesl/canvas/nmonath/grant_work/ai2/data/acl/
outputDir=$2


sh run_grobid.sh $memory $task $GROBID_ROOT $inputDir $outputDir
