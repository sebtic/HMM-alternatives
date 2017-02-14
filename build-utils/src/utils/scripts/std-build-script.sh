#!/bin/bash

nativearch=$1
nativeos=$2

# we are in /work/target
for s in /work/src/scripts/native-build-${nativearch}-${nativeos}.sh /work/target/extracted/utils/scripts/cmake-build.sh; do
	if [ -f $x ]; then
	  echo "Found script $s"
	  chmod +x $s
		$s
		exit $?
  fi
done
echo "No script found"
