#!/bin/bash

export basedir=$(pwd)
export CC=i686-pc-linux-gnu-gcc
export CXX=i686-pc-linux-gnu-g++
export CMAKE_SYSTEM_NAME=Linux
export CMAKE_SYSTEM_PROCESSOR=i386
export buildsystem=generic

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Testing with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Testing with generic build scheme..."
  . ${basedir}/extracted/utils/scripts/cmake-test.sh
fi
