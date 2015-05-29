#!/bin/sh


memory=$1
task=$2
GROBID_ROOT=$3
inputDir=$4
outputDir=$5

jarpath="$GROBID_ROOT/grobid-core/target/grobid-core-0.3.4-SNAPSHOT.one-jar.jar"
grobid_home="$GROBID_ROOT/grobid-home"
grobid_properties="$GROBID_ROOT/grobid-home/config/grobid.properties"

echo "============================================================================"
echo "[citation_coref] Running GROBID PDF processor"
echo "[citation_coref] GROBID_ROOT = $GROBID_ROOT"
echo "[citation_coref] task = $task"
echo "[citation_coref] input = $inputDir"
echo "[citation_coref] output = $outputDir"
echo "[citation_coref] Running command:"
echo java -Xmx$memory -jar $jarpath \
        -gH $grobid_home \
        -gP $grobid_properties \
        -dIn $inputDir \
        -dOut $outputDir \
        -exe $task 
echo ""
echo ""

START=$(date +%s)

java -Xmx$memory -jar $jarpath \
        -gH $grobid_home \
        -gP $grobid_properties \
        -dIn $inputDir \
        -dOut $outputDir \
        -exe $task

END=$(date +%s)
RTSECONDS=$(($END - $START))
echo "Running Time (seconds) = $RTSECONDS "
echo "============================================================================"
