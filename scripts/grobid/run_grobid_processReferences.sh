#!/bin/sh

memory=$1
GROBID_ROOT=$2
inputDir=$3
outputDir=$4

sh run_grobid.sh $memory processReferences $GROBID_ROOT $inputDir $outputDir
