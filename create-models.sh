echo "Building 4x5"
lein run src/dactyl_keyboard/dactyl.clj
echo "   right"
cp things/right.scad things/right-4x5.scad
echo "   left"
cp things/left.scad things/left-4x5.scad
echo "   plate"
cp things/right-plate.scad things/right-4x5-plate.scad
#echo "Dumpting 4x5"
#echo "   plate"
#openscad -o things/right-4x5-plate.dxf things/right-4x5-plate.scad >/dev/null 2>&1 &
#echo "   right"
#openscad -o things/right-4x5.stl things/right-4x5.scad >/dev/null 2>&1 &
#echo "   left"
#openscad -o things/left-4x5.stl  things/left-4x5.scad >/dev/null 2>&1 &

echo "Building 4x6"
patch -p1 < 4x6.patch 
lein run src/dactyl_keyboard/dactyl.clj
echo "   right"
cp things/right.scad things/right-4x6.scad
echo "   left"
cp things/left.scad things/left-4x6.scad
echo "   plate"
cp things/right-plate.scad things/right-4x6-plate.scad
#echo "Dumpting 4x6"
#echo "   plate"
#openscad -o things/right-4x6-plate.dxf things/right-4x6-plate.scad >/dev/null 2>&1 &
#echo "   right"
#openscad -o things/right-4x6.stl things/right-4x6.scad >/dev/null 2>&1  &
#echo "   left"
#openscad -o things/left-4x6.stl  things/left-4x6.scad >/dev/null 2>&1 &
git checkout src/dactyl_keyboard/dactyl.clj

echo "Building 5x6"
patch -p1 < 5x6.patch 
lein run src/dactyl_keyboard/dactyl.clj
echo "   right"
cp things/right.scad things/right-5x6.scad
echo "   left"
cp things/left.scad things/left-5x6.scad
echo "   plate"
cp things/right-plate.scad things/right-5x6-plate.scad
#echo "Dumpting 5x6"
#echo "   plate"
#openscad -o things/right-5x6-plate.dxf things/right-5x6-plate.scad >/dev/null 2>&1 &
#echo "   right"
#openscad -o things/right-5x6.stl things/right-5x6.scad >/dev/null 2>&1  &
#echo "   left"
#openscad -o things/left-5x6.stl  things/left-5x6.scad >/dev/null 2>&1 &
git checkout src/dactyl_keyboard/dactyl.clj

echo "Building 6x6"
patch -p1 < 6x6.patch 
lein run src/dactyl_keyboard/dactyl.clj
echo "   right"
cp things/right.scad things/right-6x6.scad
echo "   left"
cp things/left.scad things/left-6x6.scad
echo "   plate"
cp things/right-plate.scad things/right-6x6-plate.scad
#echo "Dumpting 6x6"
#echo "   plate"
#openscad -o things/right-6x6-plate.dxf things/right-6x6-plate.scad >/dev/null 2>&1 &
#echo "   right"
#openscad -o things/right-6x6.stl things/right-6x6.scad >/dev/null 2>&1  &
#echo "   left"
#openscad -o things/left-6x6.stl  things/left-6x6.scad >/dev/null 2>&1 &
git checkout src/dactyl_keyboard/dactyl.clj


# git add things/*-4x5.stl
# git add things/right-4x5-plate.dxf
# git commit -m "Add CAD files"
wait
