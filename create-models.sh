#!/usr/bin/env bash -e

PATCH_DIR=$(dirname "$0")/patches
echo "Creating models and support files..."

# Initial reset
git checkout src/dactyl_keyboard/dactyl.clj >/dev/null 2>&1

# Loop over each patch, generating scad, stl and plate dxf
for file in `find ${PATCH_DIR} -name "*.patch" -type f | sort -V`; do
  base=$(basename ${file} .patch)
  echo "  Running $base"

  printf "    - %-61s" "Patching base file..."
  patch -p1 < ${file} >/dev/null
  echo "done"

  printf "    - %-61s" "Building model SCAD file..."
  lein run src/dactyl_keyboard/dactyl.clj
  echo "done"

  printf "    - %-61s" "Copying files to correct names..."
  cp things/right.scad things/right-${base}.scad
  cp things/left.scad things/left-${base}.scad
  cp things/right-plate.scad things/right-${base}-plate.scad
  echo "done"

  printf "    - %-61s" "Exporting DXF and STL files..."
  openscad -o things/right-${base}-plate.dxf things/right-${base}-plate.scad >/dev/null 2>&1 &
  openscad -o things/right-${base}.stl things/right-${base}.scad >/dev/null 2>&1  &
  openscad -o things/left-${base}.stl  things/left-${base}.scad >/dev/null 2>&1 &
  wait
  echo "done"

  printf "    - %-61s" "Resetting base file to default..."
  git checkout src/dactyl_keyboard/dactyl.clj >/dev/null 2>&1
  echo "done"

  printf "    - %-61s" "Adding files to git..."
  git add things/right-${base}-plate.dxf
  git add things/right-${base}.stl
  git add things/left-${base}.stl
  echo "done"
  echo
done

# Commit to git
git commit -m "Update CAD files"
echo

echo "Done."
