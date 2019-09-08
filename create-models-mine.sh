# Debug
set -x

lein run src/dactyl_keyboard/dactyl.clj
/Applications/OpenSCAD.app/Contents/MacOS/OpenSCAD -o things/right-mine-plate.dxf things/right-plate.scad
/Applications/OpenSCAD.app/Contents/MacOS/OpenSCAD -o things/right-mine.stl things/right.scad
/Applications/OpenSCAD.app/Contents/MacOS/OpenSCAD -o things/left-mine.stl  things/left.scad
