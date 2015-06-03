#!/bin/bash

ieslpdf2text_root=$1
filenamesFile=$2
outputDir=$3
startLine=${4:-1}
endLine=${5:-$(wc -l < $filenamesFile)}


echo "==========================================================================="
echo "[citation_coref] Running iesl-pdf-to-text"
echo "[citation_coref] Input file of filenames: $filenamesFile. Lines $startLine to $endLine"
echo "[citation_coref] Output Directory: $outputDir"


START=$(date +%s)
for filename in $(sed -n "${startLine},${endLine}p" $filenamesFile) ; do 
	newfilename="${filename##*/}"
	newfilename="$outputDir/${newfilename%.*}.svg"
	echo $ieslpdf2text_root/bin/run.js --svg -f -i $filename -o $newfilename
	$ieslpdf2text_root/bin/run.js --svg -f -i $filename -o $newfilename
done
END=$(date +%s)
RTSECONDS=$(($END - $START))
echo "[citation_coref] Complete."
echo "[citation_coref] Running time: $RTSECONDS seconds"
echo "============================================================================"
