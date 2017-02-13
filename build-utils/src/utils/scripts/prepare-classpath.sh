#!/bin/bash

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

