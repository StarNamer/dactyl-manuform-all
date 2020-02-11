#!/usr/bin/env bash

docker run -t -v "$(pwd)":/app pandeiro/lein deps
docker commit $(docker ps -a | awk '/lein deps/ {print $1}' | head -1) dactyl-manuform-lein
