#!/bin/bash

echo "Trying to generate documentation for ${nativearch}-${nativeos} in $(pwd)..."
nativearch=$1
nativeos=$2

chmod a+x target/extracted/utils/scripts/*

mkdir -p target
 
# test script
SCRIPT="target/extracted/utils/scripts/native-doxygen-${nativearch}-${nativeos}.sh"
if  [ -f $SCRIPT ]; then
	echo "Found test script $SCRIPT"
	chmod a+x $SCRIPT
	echo "Running script with dockcross ..."
target/dockcross-${nativearch}-${nativeos} bash -c "cd target; bash -c /work/$SCRIPT"
	result=$?
	echo "Script has terminated"
	exit $result
else
	echo "Missing specific script. Using standard doxygen method."
	cd target
	mkdir -p extracted/generic/include extracted/debug/include extracted/release/include
  doxygen filtered-resources/Doxyfile
  cd ..
fi



