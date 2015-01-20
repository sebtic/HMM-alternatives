#!/bin/bash

if [[ -f ../src/scripts/$(basename $0) ]]; then
  echo "Generating documentation with specific build scheme..."
  . ../src/scripts/$(basename $0)
else
  echo "Generating documentation with generic build scheme..."

  mkdir -p extracted/generic/include extracted/debug/include extracted/release/include
  doxygen filtered-resources/Doxyfile
fi
