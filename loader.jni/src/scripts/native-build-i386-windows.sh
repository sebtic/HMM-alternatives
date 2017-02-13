#!/bin/bash

export basedir=$(pwd)
export CC=i686-pc-mingw32-gcc
export CXX=i686-pc-mingw32-g++
export CMAKE_SYSTEM_NAME=Windows
export CMAKE_SYSTEM_PROCESSOR=i386
export buildsystem=generic

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

    GCC_EXTRALIB=$(which libgcc_s_sjlj-1.dll)
    if [[ -f "$GCC_EXTRALIB" ]]; then
      ALTERNATIVE_EXTRA_PARAMS="-DALTERNATIVE_EXTRA_PARAMS:STRING=extractfile.1=libgcc_s_sjlj-1.dll"
    else
      ALTERNATIVE_EXTRA_PARAMS=""
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
      "$ALTERNATIVE_EXTRA_PARAMS" \
      "$ALTERNATIVE_PROCESSOR" \
      ../../../../src

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
      "$ALTERNATIVE_EXTRA_PARAMS" \
      "$ALTERNATIVE_PROCESSOR" \
      ../../../../src \
      && make
      errorcode=$?
    [[ "$errorcode" != "0" ]] && exit $errorcode

    mkdir -p ${basedir}/alternatives/$pathQualifier
    if [[ -f "$GCC_EXTRALIB" ]]; then
      cp "$GCC_EXTRALIB" ${basedir}/alternatives/$pathQualifier/
    fi
    res=$(cp ${basedir}/build/$pathQualifier/*.dll ${basedir}/alternatives/$pathQualifier/ 2>&1 )
    res=$(cp ${basedir}/build/$pathQualifier/*.so ${basedir}/alternatives/$pathQualifier/ 2>&1 )
    res=$(cp ${basedir}/build/$pathQualifier/*.dylib ${basedir}/alternatives/$pathQualifier/ 2>&1)
    res=$(cp ${basedir}/build/$pathQualifier/library.alternatives ${basedir}/alternatives/$pathQualifier/ 2>&1 )

    if [[ "$buildsystem" == "default" ]]; then
      # global config.alternatives
      libname=$(cat ${basedir}/build/$pathQualifier/library.alternatives|grep "name=")
      echo "${libname}" > ${basedir}/alternatives/config.alternatives
      echo "similarproperty.1=require.alternatives.requirements.Cpu" >> ${basedir}/alternatives/config.alternatives
      echo "similarproperty.2=alternatives.Cpu" >> ${basedir}/alternatives/config.alternatives
    fi
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