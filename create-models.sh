#!/usr/bin/env bash
rm -rf things/*
mkdir -p things

echo "running lein"
lein run src/dactyl_keyboard/dactyl.clj

echo "generating switch-hole.stl"
openscad -o things/switch-hole.stl things/switch-hole.scad

echo "generating teensy-holder.stl"
openscad -o things/teensy-holder.stl things/teensy-holder.scad

echo "generating right-plate-outline.dxf"
openscad -o things/right-plate-outline.dxf things/right-plate-outline.scad

echo "generating right-case.stl"
openscad -o things/right-case.stl things/right-case.scad

echo "generating left-case.stl"
openscad -o things/left-case.stl things/left-case.scad
