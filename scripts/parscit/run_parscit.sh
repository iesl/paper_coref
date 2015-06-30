#!/bin/bash

parscit_root=$1
command=$2
filenamesFile=$3
outputDir=$4
startLine=${5:-1}
endLine=${6:-$(wc -l < $filenamesFile)}

echo "==========================================================================="
echo "[citation_coref] Running parscit"
echo "[citation_coref] Input file of filenames: $filenamesFile. Lines $startLine to $endLine"
echo "[citation_coref] Output Directory: $outputDir"

mkdir -p $outputDir

START=$(date +%s)
for filename in $(sed -n "${startLine},${endLine}p" $filenamesFile) ; do  
        newfilename="${filename##*/}"
        newfilename="$outputDir/${newfilename%.*}.xml"
        echo $parscit_root/bin/citeExtract.pl -m $command $filename $newfilename
        $parscit_root/bin/citeExtract.pl -m $command $filename $newfilename
done
END=$(date +%s)
RTSECONDS=$(($END - $START))
echo "[citation_coref] Complete."
echo "[citation_coref] Running time: $RTSECONDS seconds"
echo "============================================================================"
