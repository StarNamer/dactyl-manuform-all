#!/bin/sh
#
# 1: First of all execute "start-docker.sh" before build dactyl manuform
# 2: and then execute this script every time you build

# compile clj --> scad file in the docker container
lein run && echo '-> things/*.scad'

