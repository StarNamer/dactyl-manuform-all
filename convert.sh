#!/bin/bash

for f in $(find ./things/ -type f -name "*.scad") ; do
    base=$(basename $f)
    dir=$(dirname $f)
    openscad -o ${dir}/${base}.stl $f
done
