#!/bin/bash

export basedir=$(pwd)
export CMAKE_SYSTEM_NAME=Linux
export CMAKE_SYSTEM_PROCESSOR=x86_64
export buildsystem=default

/work/target/extracted/utils/scripts/std-build-script.sh amd64 linux
