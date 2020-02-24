#!/bin/bash

set -e

rm -f things/*.scad
rm -f things/*.stl
rm -f things/*.dxf

function build () {

    export ROWS=$1
    export COLS=$2

    for SWITCH_TYPE in "cherrymx" "kailh"; do
        for WIRE_POSTS in "yes" "no"; do

            LAYOUT="${ROWS}x${COLS}"
            echo "Build $LAYOUT, switch:$SWITCH_TYPE, wire_posts:$WIRE_POSTS"
            POSTFIX="$LAYOUT-$SWITCH_TYPE-wire_posts_$WIRE_POSTS"
            LEFT="left-$POSTFIX"
            RIGHT="right-$POSTFIX"

            export SWITCH_TYPE
            export WIRE_POSTS

            lein run src/dactyl_keyboard/dactyl.clj
            mv things/right.scad things/$RIGHT.scad
            mv things/left.scad things/$LEFT.scad
            mv things/right-plate.scad things/right-$LAYOUT-plate.scad
            openscad -o things/right-$LAYOUT-plate.dxf things/right-$LAYOUT-plate.scad >/dev/null 2>&1
            openscad -o things/$RIGHT.stl things/$RIGHT.scad >/dev/null 2>&1
            openscad -o things/$LEFT.stl  things/$LEFT.scad >/dev/null 2>&1
        done
    done
}

build 4 5
build 4 6
build 5 6
build 6 6
