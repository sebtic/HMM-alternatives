#!/bin/bash

echo "Trying to build for ${nativearch}-${nativeos} in $(pwd)..."
nativearch=$1
nativeos=$2

chmod a+x target/extracted/utils/scripts/*

mkdir -p target

# pull dockcross
echo "Pulling dockcross..."
cd target
bash extracted/utils/scripts/pull-dockcross-${nativearch}-${nativeos}.sh > /dev/null
cd ..

# fix classpath

cd target

echo "Fixing classpath for dockcross..."
IFS=':' read -r -a array <<< "$(<javah-classpath)"

echo -n "" > new-javah-classpath

mkdir -p classpath
c=1
for element in "${array[@]}"
do
	case $element in
		*.m2/*)
			d=$(dirname $element | sed "s/.*\/\.m2\//.m2\//")
			mkdir -p classpath/$d
			cp $element classpath/$d/
			echo -n "/work/target/classpath/$d/$(basename $element):" >> new-javah-classpath
			;;
		*.jar)
			cp $element classpath/alternatives-target-$c.jar 
			echo -n "/work/target/classpath/alternatives-target-$c.jar:" >> new-javah-classpath
			c=$(( c + 1 ))
			;;
		*target/classes)
			echo -n "/work/target/classes:" >> new-javah-classpath
		;;
	esac
done
mv new-javah-classpath javah-classpath
cd ..
 
# build script
SCRIPT="target/extracted/utils/scripts/native-build-${nativearch}-${nativeos}.sh"
if  [ -f $SCRIPT ]; then
	echo "Found build script $SCRIPT"
	chmod a+x $SCRIPT
	echo "Running script with dockcross ..."
	if [ -z "$DOCKCROSS_DNS" ]; then
    target/dockcross-${nativearch}-${nativeos} bash -c "export PARALLEL_BUILD=$PARALLEL_BUILD; cd target; bash -c /work/$SCRIPT"
  else
    target/dockcross-${nativearch}-${nativeos} -a --dns=$DOCKCROSS_DNS bash -c "export PARALLEL_BUILD=$PARALLEL_BUILD; cd target; bash -c /work/$SCRIPT"
  fi
	result=$?
	echo "Script has terminated"
	exit $result
else
	echo "Missing build script. Nothing to do."
fi



