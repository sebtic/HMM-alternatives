#!/bin/bash

export basedir=$(pwd)
export CMAKE_SYSTEM_NAME=Linux
export CMAKE_SYSTEM_PROCESSOR=i386
export buildsystem=generic

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Buindling with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Buindling with generic build scheme..."
  . ${basedir}/extracted/utils/scripts/cmake-build.sh
fi
