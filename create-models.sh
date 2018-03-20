lein run src/dactyl_keyboard/dactyl.clj
cp things/right.scad things/right-5x7.scad
cp things/left.scad things/left-5x7.scad
openscad -o things/right-5x7.stl things/right-5x7.scad >/dev/null 2>&1 &
openscad -o things/left-5x7.stl  things/left-5x7.scad >/dev/null 2>&1 &
wait
