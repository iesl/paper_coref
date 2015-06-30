#!/bin/bash

filenamesFile=$1
num_segments=$2
outputBasename=$3

endLine=$(wc -l < $filenamesFile)
batch_size=$((endLine / num_segments))


counter=0
while [ $counter -lt $((num_segments - 1)) ]; do
    first=$((counter * batch_size + 1))
    last=$(( (counter + 1 ) * batch_size ))
    newfilename="$outputBasename-$counter.txt"
	sed -n "${first},${last}p" $filenamesFile  > $newfilename
	counter=$((counter + 1))
done

if [ $((  counter * batch_size  )) -lt $endLine ]; then
    newfilename="$outputBasename-$counter.txt"
    first=$((  counter * batch_size  + 1 ))
    last=$endLine
	sed -n "${first},${last}p" $filenamesFile  > $newfilename
fi

