#!/bin/sh
#
# 1: First of all execute this script before build dactyl-manuform
# 2: and then execute "build.sh" every time you build

IMAGE_NAME=dactyl
CONTAINER_NAME=dactyl-builder

# prepare docker container
# docker build . -t IMAGE_NAME

# start docker container interactively
docker run -v $(pwd)/src:/dactyl/src -v $(pwd)/things:/dactyl/things -it --name $CONTAINER_NAME $IMAGE_NAME /bin/bash

# next, execute 'src/build.sh' in the container



# exit container? don't worry, type following line to return into the container
# docker attach dactyl-builder

