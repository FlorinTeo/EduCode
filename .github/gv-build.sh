#!/usr/bin/env bash

#
# Build script that creates a build artifact for the GraphVisualizer web app
#

rm -rf .gv-build

mkdir .gv-build/
cp index.html index.css favicon.ico .gv-build/
cp -r GraphVisualizer/ .gv-build/
