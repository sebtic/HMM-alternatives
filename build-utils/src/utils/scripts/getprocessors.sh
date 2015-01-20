#!/bin/bash

# $1 = generic / default
# $2 compilable / runnable

alternatives_processors_intel="pentium pentium-mmx pentiumpro i686 pentium2 pentium3 pentium-m pentium4 prescott nocona core2"
alternatives_processors_amd="k6 k6-2 athlon athlon-4 k8 k8-sse3 amdfam10"

[[ -z $alternatives_processors ]] && \
  alternatives_processors="pentium pentium-mmx pentiumpro i686 pentium2 pentium3 pentium-m pentium4 prescott nocona core2 k6 k6-2 athlon athlon-4 k8 k8-sse3 amdfam10"

if [[ "$1" == "generic" ]]; then
  echo generic
  exit 0
else
  if [[ "$1" == "default" ]]; then
    available=""
    echo "int main(void){return 0;}" > test.c
    for p in $alternatives_processors; do
      (gcc -march=$p -mtune=$p test.c 2>test.log >test.log) && available="$available $p"
    done

    if [[ "$2" == "compilable" ]]; then
      echo $available
      exit 0
    fi
    if [[ "$2" == "runnable" ]]; then
      if [[ -n $(cat /proc/cpuinfo |grep Intel) ]]; then
        for x in $available; do
          if [[ -n $(echo $alternatives_processors_intel | grep $x) ]]; then
            echo $x;
            exit 0
          fi
        done
      else
        for x in $available; do
          if [[ -n $(echo $alternatives_processors_amd | grep $x) ]]; then
            echo $x;
            exit 0
          fi
        done
      fi
    fi
  fi
fi
