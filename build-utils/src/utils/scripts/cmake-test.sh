#!/bin/bash

basedir=$(pwd)

[[ "$buildsystem" == "generic" ]] && modes=generic
[[ "$buildsystem" == "default" ]] && modes="debug release"

processor=$(${basedir}/extracted/utils/scripts/getprocessors.sh $buildsystem runnable)

for mode in $modes; do
  echo "Testing ${mode} version..."
  cd ${basedir}/build/$processor/$mode

  if [[ -z $(echo "$CXX" | grep mingw32) ]]; then
    PATH=${PATH}:${basedir}/extracted/generic/lib:${basedir}/extracted/${mode}/lib ctest -V
  else
    LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${basedir}/extracted/generic/lib:${basedir}/extracted/${mode}/lib ctest -V
  fi
done
