#!/bin/bash

diagram=$1
background=$2
output=$3
dim=$(identify -format "%[fx:w]x%[fx:h]" $diagram)

composite -tile grid.png -size $dim xc:none png:- | convert - -negate $background

convert $background $diagram -flatten $output
