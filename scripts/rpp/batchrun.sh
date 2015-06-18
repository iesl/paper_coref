#!/bin/bash

# from kate's script

root=$1
lexicons=$2
citeCRF="file://$root/citationCRF.factorie"
headerCRF="$root/headerCRF.factorie"
dataFilesFile=$3
outputDir=$4

CP="$root/target/classes:$root/target/rpp-0.1-SNAPSHOT-jar-with-dependencies.jar"

mem="4G"

java -Xmx${mem} -cp $CP -Dcc.factorie.app.nlp.lexicon.Lexicon=$lexicons edu.umass.cs.iesl.rpp.BatchMain \
--reference-model-uri=$citeCRF \
--header-tagger-model=$headerCRF \
--data-files-file=$dataFilesFile \
--output-dir=$outputDir
