#!/bin/bash

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Cleaning with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Cleaning with generic build scheme..."
fi
