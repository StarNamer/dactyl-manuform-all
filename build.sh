#!/bin/sh

# prepare docker container
# docker build . -t dactyl

# create dactyl scad file
docker run -v $(pwd)/src:/dactyl/src -v $(pwd)/things:/dactyl/things dactyl
echo '-> things/*.scad'
