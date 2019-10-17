lein run src/dactyl_keyboard/dactyl.clj
openscad -o things/right-plate-outline.dxf things/right-plate.scad >/dev/null 2>&1 &
openscad -o things/right.stl things/right.scad >/dev/null 2>&1 &
openscad -o things/left.stl  things/left.scad >/dev/null 2>&1 &

wait
