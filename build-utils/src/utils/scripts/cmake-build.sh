#!/bin/bash

if [[ ! -f ../src/CMakeLists.txt ]]; then
    echo "Missing CMakeLists.txt in src. Continuing without compilation."
    exit 0
fi


chmod +x ${basedir}/extracted/utils/scripts/getprocessors.sh

# compile all alternatives

[[ "$buildsystem" == "generic" ]] && modes=generic
[[ "$buildsystem" == "default" ]] && modes="debug release"

for processor in $(${basedir}/extracted/utils/scripts/getprocessors.sh $buildsystem compilable); do
  for mode in $modes; do

    echo "Building for $processor-$mode..."

    pathQualifier=$processor/$mode

    rm -rf $basedir/build/$pathQualifier 2>/dev/null
    mkdir -p $basedir/build/$pathQualifier
    cd $basedir/build/$pathQualifier

    [[ "$mode" == "debug" ]] && CMAKE_BUILD_TYPE=Debug
    [[ "$mode" == "release" ]] && CMAKE_BUILD_TYPE=Release
    [[ "$mode" == "generic" ]] && CMAKE_BUILD_TYPE=Release
    [[ -z $mode ]] && echo "mode must be defined" && exit 1

    [[ "$CMAKE_SYSTEM_PROCESSOR" == "x86_64" ]] && ALTERNATIVE_ARCH=amd64
    [[ "$CMAKE_SYSTEM_PROCESSOR" == "i386" ]] && ALTERNATIVE_ARCH="x86|i386"
    [[ -z $CMAKE_SYSTEM_PROCESSOR ]] && echo "CMAKE_SYSTEM_PROCESSOR must be defined" && exit 1


    if [[ -n $(uname|grep MINGW32) ]]; then
      CMAKE_GENERATOR="-G \"MSYS Makefiles\""
    else
      CMAKE_GENERATOR=""
    fi

    if [[ -z $(echo "$CXX" | grep mingw32) ]]; then
      CMAKE_WIN32=""
    else
      CMAKE_WIN32="-DWIN32:STRING=1"
    fi


    if [[ -z $CROSSDEV ]]; then
      CMAKE_CROSSDEV=""
    else
      CMAKE_CROSSDEV="-DCROSSDEV:STRING=1"
    fi

    if [[ "$processor" == "generic" ]]; then
      ALTERNATIVE_PROCESSOR=""
    else
      ALTERNATIVE_PROCESSOR="-DALTERNATIVE_PROCESSOR:STRING=$processor"
    fi

    echo Running cmake \
      $CMAKE_GENERATOR \
      "-DCMAKE_BUILD_TYPE=$CMAKE_BUILD_TYPE" \
      "-DALTERNATIVE_ARCH:STRING=$ALTERNATIVE_ARCH" \
      "-DALTERNATIVE_VERSION:STRING=$(<${basedir}/version)" \
      "$CMAKE_WIN32" \
      "$CMAKE_CROSSDEV" \
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
      $CMAKE_GENERATOR \
      "-DCMAKE_BUILD_TYPE=$CMAKE_BUILD_TYPE" \
      "-DALTERNATIVE_ARCH:STRING=$ALTERNATIVE_ARCH" \
      "-DALTERNATIVE_VERSION:STRING=$(<${basedir}/version)" \
      "$CMAKE_WIN32" \
      "$CMAKE_CROSSDEV" \
      "-DCMAKE_SYSTEM_NAME:STRING=$CMAKE_SYSTEM_NAME" \
      "-DCMAKE_C_COMPILER:STRING=$CC" \
      "-DCMAKE_CXX_COMPILER:STRING=$CXX" \
      "-DCMAKE_SYSTEM_PROCESSOR:STRING=$CMAKE_SYSTEM_PROCESSOR" \
      "$ALTERNATIVE_PROCESSOR" \
      ../../../../src \
      && make $MAKEPARAMS
      errorcode=$?
    [[ "$errorcode" != "0" ]] && exit $errorcode

    mkdir -p ${basedir}/alternatives/$pathQualifier
    res=$(cp ${basedir}/build/$pathQualifier/*.dll ${basedir}/alternatives/$pathQualifier/ 2>&1 )
    res=$(cp ${basedir}/build/$pathQualifier/*.so ${basedir}/alternatives/$pathQualifier/ 2>&1 )
    res=$(cp ${basedir}/build/$pathQualifier/*.dylib ${basedir}/alternatives/$pathQualifier/ 2>&1)
    res=$(cp ${basedir}/build/$pathQualifier/library.alternatives ${basedir}/alternatives/$pathQualifier/ 2>&1 )
  done
done

for mode in $modes; do
  pathQualifier=$(${basedir}/extracted/utils/scripts/getprocessors.sh $buildsystem runnable)/$mode

  # preparer an alternative for latter usage
  mkdir -p ${basedir}/native/${mode}/lib ${basedir}/native/${mode}/include ${basedir}/native/${mode}/config

  res=$(cp ${basedir}/build/$pathQualifier/*.so ${basedir}/native/${mode}/lib/ 2>&1)
  res=$(cp ${basedir}/build/$pathQualifier/*.a ${basedir}/native/${mode}/lib/ 2>&1)
  res=$(cp ${basedir}/build/$pathQualifier/*.lib ${basedir}/native/${mode}/lib/ 2>&1)
  res=$(cp ${basedir}/build/$pathQualifier/*.dll ${basedir}/native/${mode}/lib/ 2>&1)
  res=$(cp ${basedir}/build/$pathQualifier/*.dylib ${basedir}/native/${mode}/lib/ 2>&1)

  res=$(cp -r ${basedir}/../src/main/include ${basedir}/native/${mode}/ 2>&1)

  res=$(cp -r ${basedir}/build/$pathQualifier/*.pc ${basedir}/native/${mode}/config/ 2>&1)
done
