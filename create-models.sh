#!/bin/bash

set -e

rm -f things/*.scad
rm -f things/*.stl
rm -f things/*.dxf

for SWITCH in "cherrymx" "kailh"; do

    echo "Build $SWITCH"
    export SWITCH_TYPE=$SWITCH

    lein run src/dactyl_keyboard/dactyl.clj
    mv things/right.scad things/right-4x5-$SWITCH.scad
    mv things/left.scad things/left-4x5-$SWITCH.scad
    mv things/right-plate.scad things/right-4x5-plate-$SWITCH.scad
    openscad -o things/right-4x5-plate-$SWITCH.dxf things/right-4x5-plate-$SWITCH.scad >/dev/null 2>&1 &
    openscad -o things/right-4x5-$SWITCH.stl things/right-4x5-$SWITCH.scad >/dev/null 2>&1 &
    openscad -o things/left-4x5-$SWITCH.stl  things/left-4x5-$SWITCH.scad >/dev/null 2>&1 &

    # patch -p1 < 4x6.patch 
    # lein run src/dactyl_keyboard/dactyl.clj
    # cp things/right.scad things/right-4x6.scad
    # cp things/left.scad things/left-4x6.scad
    # cp things/right-plate.scad things/right-4x6-plate.scad
    # openscad -o things/right-4x6-plate.dxf things/right-4x6-plate.scad >/dev/null 2>&1 &
    # openscad -o things/right-4x6.stl things/right-4x6.scad >/dev/null 2>&1  &
    # openscad -o things/left-4x6.stl  things/left-4x6.scad >/dev/null 2>&1 &
    # git checkout src/dactyl_keyboard/dactyl.clj

    # patch -p1 < 5x6.patch 
    # lein run src/dactyl_keyboard/dactyl.clj
    # cp things/right.scad things/right-5x6.scad
    # cp things/left.scad things/left-5x6.scad
    # cp things/right-plate.scad things/right-5x6-plate.scad
    # openscad -o things/right-5x6-plate.dxf things/right-5x6-plate.scad >/dev/null 2>&1 &
    # openscad -o things/right-5x6.stl things/right-5x6.scad >/dev/null 2>&1  &
    # openscad -o things/left-5x6.stl  things/left-5x6.scad >/dev/null 2>&1 &
    # git checkout src/dactyl_keyboard/dactyl.clj

    # patch -p1 < 6x6.patch 
    # lein run src/dactyl_keyboard/dactyl.clj
    # cp things/right.scad things/right-6x6.scad
    # cp things/left.scad things/left-6x6.scad
    # cp things/right-plate.scad things/right-6x6-plate.scad
    # openscad -o things/right-6x6-plate.dxf things/right-6x6-plate.scad >/dev/null 2>&1 &
    # openscad -o things/right-6x6.stl things/right-6x6.scad >/dev/null 2>&1  &
    # openscad -o things/left-6x6.stl  things/left-6x6.scad >/dev/null 2>&1 &
    # git checkout src/dactyl_keyboard/dactyl.clj

done


# # git add things/*-4x5.stl
# # git add things/right-4x5-plate.dxf
# # git commit -m "Add CAD files"
# wait