#!/bin/bash

export basedir=$(pwd)
export CMAKE_SYSTEM_NAME=Windows
export CMAKE_SYSTEM_PROCESSOR=i386
export buildsystem=generic

/work/target/extracted/utils/scripts/std-build-script.sh i386 windows
