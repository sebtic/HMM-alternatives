#!/bin/bash

nativearch=$1
nativeos=$2

# we are in /work/target
for s in /work/src/scripts/native-test-${nativearch}-${nativeos}.sh /work/target/extracted/utils/scripts/cmake-test.sh; do
	if [ -f $s ]; then
	  echo "Found script $s"
	  chmod a+x $s
		$s
		exit $?
  fi
done
echo "No script found"
