#!/bin/bash

echo "Trying to test for ${nativearch}-${nativeos} in $(pwd)..."
nativearch=$1
nativeos=$2

chmod a+x target/extracted/utils/scripts/*

mkdir -p target
 
# test script
SCRIPT="target/extracted/utils/scripts/native-test-${nativearch}-${nativeos}.sh"
if  [ -f $SCRIPT ]; then
	echo "Found test script $SCRIPT"
	chmod a+x $SCRIPT
	echo "Running script with dockcross ..."
	if [ -z "$DOCKCROSS_DNS" ]; then
    target/dockcross-${nativearch}-${nativeos} bash -c "cd target; bash -c /work/$SCRIPT"
  else
    target/dockcross-${nativearch}-${nativeos} -a --dns=$DOCKCROSS_DNS bash -c "cd target; bash -c /work/$SCRIPT"
  fi
	result=$?
	echo "Script has terminated"
	exit $result
else
	echo "Missing test script. Nothing to do."
fi



