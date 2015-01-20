#!/bin/bash

export basedir=$(pwd)
export CC=x86_64-pc-mingw32-gcc
export CXX=x86_64-pc-mingw32-g++
export CMAKE_SYSTEM_NAME=Windows
export CMAKE_SYSTEM_PROCESSOR=x86_64
export buildsystem=generic

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Testing with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Testing with generic build scheme..."
  . ${basedir}/extracted/utils/scripts/cmake-test.sh
fi
