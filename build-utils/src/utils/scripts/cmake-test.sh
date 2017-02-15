#!/bin/bash

if [[ ! -f /work/src/CMakeLists.txt ]]; then
    echo "Missing CMakeLists.txt in src. Continuing without testing."
    exit 0
fi

[[ "$buildsystem" == "generic" ]] && modes=generic
[[ "$buildsystem" == "default" ]] && modes="debug release"

export SDKMAN_DIR=/work/target/sdkman
source "$SDKMAN_DIR/bin/sdkman-init.sh"

processor=$(/work/target/extracted/utils/scripts/getprocessors.sh $buildsystem runnable)

for mode in $modes; do
  echo "Testing ${mode} version..."
  cd /work/target/build/$processor/$mode

  LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/work/target/extracted/generic/lib:/work/target/extracted/${mode}/lib ctest -V
done
