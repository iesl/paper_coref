#!/bin/bash
inputdir=${1:-"/iesl/canvas/nmonath/grant_work/ai2/data/acl/"}
outputdir=${2:-"/iesl/canvas/nmonath/grant_work/ai2/data/pdftotext-acl-processed"}


echo "==========================================================================="
echo "[citation_coref] Running pdftotext (Linux)"
echo "[citation_coref] Input Directory: $inputdir"
echo "[citation_coref] Output Directory: $outputDir"

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
