patch -p1 < 5x7.patch 
lein run src/dactyl_keyboard/dactyl.clj
cp things/right.scad things/right-5x7.scad
cp things/left.scad things/left-5x7.scad
cp things/right-plate.scad things/right-5x7-plate.scad
openscad -o things/right-5x7-plate.dxf things/right-5x7-plate.scad >/dev/null 2>&1 &
openscad -o things/right-5x7.stl things/right-5x7.scad >/dev/null 2>&1  &
openscad -o things/left-5x7.stl  things/left-5x7.scad >/dev/null 2>&1 &
git checkout src/dactyl_keyboard/dactyl.clj

wait
