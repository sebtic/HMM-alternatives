#!/bin/bash

export basedir=$(pwd)
export CC=x86_64-pc-mingw32-gcc
export CXX=x86_64-pc-mingw32-g++
export CMAKE_SYSTEM_NAME=Windows
export CMAKE_SYSTEM_PROCESSOR=x86_64
export buildsystem=default

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Buindling with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Buindling with generic build scheme..."
  . ${basedir}/extracted/utils/scripts/cmake-build.sh
fi
