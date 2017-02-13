#!/bin/bash

export basedir=$(pwd)
export CC=x86_64-pc-linux-gnu-gcc
export CXX=x86_64-pc-linux-gnu-g++
export CMAKE_SYSTEM_NAME=Linux
export CMAKE_SYSTEM_PROCESSOR=x86_64
export buildsystem=default

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Buindling with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Buindling with generic build scheme..."
  . ${basedir}/extracted/utils/scripts/cmake-build.sh
fi