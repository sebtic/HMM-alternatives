#!/bin/bash

export basedir=$(pwd)
export CMAKE_SYSTEM_NAME=Windows
export CMAKE_SYSTEM_PROCESSOR=x86_64
export buildsystem=default

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Testing with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Testing with generic build scheme..."
  . ${basedir}/extracted/utils/scripts/cmake-test.sh
fi
