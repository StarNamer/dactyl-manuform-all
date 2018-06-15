#!/bin/bash

#build docker image and create container:
#docker build . -t dactyl

docker run -it -v /home/qiang/dactyl-keyboard/src:/dactyl/src -v /home/qiang/dactyl-keyboard/things:/dactyl/things dactyl


