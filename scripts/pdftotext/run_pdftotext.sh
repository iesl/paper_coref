#!/bin/bash
inputdir=$1
outputdir=$2


echo "==========================================================================="
echo "[citation_coref] Running pdftotext (Linux)"
echo "[citation_coref] Input Directory: $inputdir"
echo "[citation_coref] Output Directory: $outputdir"

START=$(date +%s)
for filename in $inputdir/*.pdf; do
	newfilename="${filename##*/}"
	newfilename="$outputdir/${newfilename%.*}.txt"
	echo pdftotext $filename $newfilename
	pdftotext $filename $newfilename
done
END=$(date +%s)
RTSECONDS=$(($END - $START))
echo "[citation_coref] Complete."
echo "[citation_coref] Running time: $RTSECONDS seconds"
echo "============================================================================"
