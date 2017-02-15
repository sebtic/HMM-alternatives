#!/bin/bash

if [[ ! -f /work/src/CMakeLists.txt ]]; then
    echo "Missing CMakeLists.txt in src. Continuing without compilation."
    exit 0
fi

export SDKMAN_DIR=/work/target/sdkman
curl -s get.sdkman.io | bash 
echo "sdkman_auto_answer=true" > $SDKMAN_DIR/etc/config
echo "sdkman_auto_selfupdate=false" >> $SDKMAN_DIR/etc/config
echo "sdkman_insecure_ssl=false" >> $SDKMAN_DIR/etc/config
source "$SDKMAN_DIR/bin/sdkman-init.sh" 
sdk install java
source "$SDKMAN_DIR/bin/sdkman-init.sh"

chmod +x /work/target/extracted/utils/scripts/getprocessors.sh

# compile all alternatives

[[ "$buildsystem" == "generic" ]] && modes=generic
[[ "$buildsystem" == "default" ]] && modes="debug release"

for processor in $(/work/target/extracted/utils/scripts/getprocessors.sh $buildsystem compilable); do
  for mode in $modes; do

    echo "Building for $processor-$mode..."

    pathQualifier=$processor/$mode

    rm -rf /work/target/build/$pathQualifier 2>/dev/null
    mkdir -p /work/target/build/$pathQualifier
    cd /work/target/build/$pathQualifier

    [[ "$mode" == "debug" ]] && CMAKE_BUILD_TYPE=Debug
    [[ "$mode" == "release" ]] && CMAKE_BUILD_TYPE=Release
    [[ "$mode" == "generic" ]] && CMAKE_BUILD_TYPE=Release
    [[ -z $mode ]] && echo "mode must be defined" && exit 1

    [[ "$CMAKE_SYSTEM_PROCESSOR" == "x86_64" ]] && ALTERNATIVE_ARCH=amd64
    [[ "$CMAKE_SYSTEM_PROCESSOR" == "i386" ]] && ALTERNATIVE_ARCH="x86|i386"
    [[ -z $CMAKE_SYSTEM_PROCESSOR ]] && echo "CMAKE_SYSTEM_PROCESSOR must be defined" && exit 1

    if [[ "$processor" == "generic" ]]; then
      ALTERNATIVE_PROCESSOR=""
    else
      ALTERNATIVE_PROCESSOR="-DALTERNATIVE_PROCESSOR:STRING=$processor"
    fi

    echo Running cmake \
      "-DCMAKE_BUILD_TYPE=$CMAKE_BUILD_TYPE" \
      "-DALTERNATIVE_ARCH:STRING=$ALTERNATIVE_ARCH" \
    "-DALTERNATIVE_VERSION:STRING=$(</work/target/version)" \
      "-DCMAKE_SYSTEM_NAME:STRING=$CMAKE_SYSTEM_NAME" \
      "-DCMAKE_C_COMPILER:STRING=$CC" \
      "-DCMAKE_CXX_COMPILER:STRING=$CXX" \
      "-DCMAKE_SYSTEM_PROCESSOR:STRING=$CMAKE_SYSTEM_PROCESSOR" \
      "$ALTERNATIVE_PROCESSOR" \
      ../../../../src
      
    if [[ -n "${PARALLEL_BUILD}" ]]; then
	MAKEPARAMS="-j $(grep -c ^processor /proc/cpuinfo)"
	echo "Using parallel build for make with $MAKEPARAMS"
    fi

    cmake \
      "-DCMAKE_BUILD_TYPE=$CMAKE_BUILD_TYPE" \
      "-DALTERNATIVE_ARCH:STRING=$ALTERNATIVE_ARCH" \
    "-DALTERNATIVE_VERSION:STRING=$(</work/target/version)" \
      "-DCMAKE_SYSTEM_NAME:STRING=$CMAKE_SYSTEM_NAME" \
      "-DCMAKE_C_COMPILER:STRING=$CC" \
      "-DCMAKE_CXX_COMPILER:STRING=$CXX" \
      "-DCMAKE_SYSTEM_PROCESSOR:STRING=$CMAKE_SYSTEM_PROCESSOR" \
      "$ALTERNATIVE_PROCESSOR" \
      ../../../../src \
      && make $MAKEPARAMS
      errorcode=$?
    [[ "$errorcode" != "0" ]] && exit $errorcode

    mkdir -p /work/target/alternatives/$pathQualifier
    res=$(cp /work/target/build/$pathQualifier/*.dll /work/target/alternatives/$pathQualifier/ 2>&1 )
    res=$(cp /work/target/build/$pathQualifier/*.so /work/target/alternatives/$pathQualifier/ 2>&1 )
    res=$(cp /work/target/build/$pathQualifier/*.dylib /work/target/alternatives/$pathQualifier/ 2>&1)
    res=$(cp /work/target/build/$pathQualifier/library.alternatives /work/target/alternatives/$pathQualifier/ 2>&1 )
  done
done

for mode in $modes; do
  pathQualifier=$(/work/target/extracted/utils/scripts/getprocessors.sh $buildsystem runnable)/$mode

  # preparer an alternative for latter usage
  mkdir -p /work/target/native/${mode}/lib /work/target/native/${mode}/include /work/target/native/${mode}/config

  res=$(cp /work/target/build/$pathQualifier/*.so /work/target/native/${mode}/lib/ 2>&1)
  res=$(cp /work/target/build/$pathQualifier/*.a /work/target/native/${mode}/lib/ 2>&1)
  res=$(cp /work/target/build/$pathQualifier/*.lib /work/target/native/${mode}/lib/ 2>&1)
  res=$(cp /work/target/build/$pathQualifier/*.dll /work/target/native/${mode}/lib/ 2>&1)
  res=$(cp /work/target/build/$pathQualifier/*.dylib /work/target/native/${mode}/lib/ 2>&1)

  res=$(cp -r /work/target/../src/main/include /work/target/native/${mode}/ 2>&1)

  res=$(cp -r /work/target/build/$pathQualifier/*.pc /work/target/native/${mode}/config/ 2>&1)
done
