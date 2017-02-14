#!/bin/bash

echo "Trying to build for ${nativearch}-${nativeos} in $(pwd)..."
nativearch=$1
nativeos=$2

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
	chmod +x $SCRIPT
	echo "Running script with dockcross ..."
target/dockcross-${nativearch}-${nativeos} bash -c "export HOME=/root; cd /work/target; ls -al; bash $SCRIPT"
	result=$?
	echo "Script has terminated"
	exit $result
else
	echo "Missing build script. Nothing to do."
fi



