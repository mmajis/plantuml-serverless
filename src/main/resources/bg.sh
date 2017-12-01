#!/bin/bash

echo "Grid background script starting"

diagram=$1
tile=$2
background=$3
output=$4
dim=$(identify -format "%[fx:w]x%[fx:h]" $diagram)

composite -tile $tile -size $dim xc:none png:- | convert - -negate $background

convert $background $diagram -flatten $output
