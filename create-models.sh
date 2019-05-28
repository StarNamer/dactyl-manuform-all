#!/bin/sh

clj -m dactyl
openscad -o things/right.stl things/right.scad
openscad -o things/left.stl  things/left.scad
