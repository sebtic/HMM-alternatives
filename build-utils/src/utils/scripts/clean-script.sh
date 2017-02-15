#!/bin/bash

nativearch=$1
nativeos=$2

# we are in /work/target
for s in /work/src/scripts/native-clean-${nativearch}-${nativeos}.sh /work/target/extracted/utils/scripts/native-clean-${nativearch}-${nativeos}.shdo
	if [ -f $s ]; then
	  echo "Found script $s"
	  chmod a+x $s
		$s
		exit $?
  fi
done
echo "No script found. Removing target directory"
rm -rf /work/target/build /work/target/native
