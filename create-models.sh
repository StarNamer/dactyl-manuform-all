#!/bin/sh

echo "Building SCAD models..."

clj -m dactyl

echo "Converting SCAD -> STL. This might take minutes..."

openscad -o things/right.stl things/right.scad &
pid1=$!
openscad -o things/left.stl  things/left.scad &
pid2=$!

wait $pid1
wait $pid2

echo "Done!"
