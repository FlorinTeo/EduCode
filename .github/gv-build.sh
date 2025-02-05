#!/usr/bin/env bash

#
# Simple build script that just copies the app into the build/ directory.
#

rm -rf .gv-build
mkdir .gv-build/
cp -r GraphVisualizer/*.ico GraphVisualizer/*.html GraphVisualizer/*.css GraphVisualizer/js GraphVisualizer/res GraphVisualizer/data .gv-build/