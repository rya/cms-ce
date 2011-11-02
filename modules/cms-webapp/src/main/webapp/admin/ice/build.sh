#!/bin/bash

echo "Searching for \$( in source files...If found, the $ function should be renamed $ice"
find ./src/*.js -exec grep "\$(" '{}' \; -print
echo "Concatenating files..."
cat ./src/jquery-1.3.2.min.js ./src/jquery.drag.js ./src/Global.js ./src/Setup.js ./src/PageOverlay.js ./src/PortletOverlay.js ./src/Tooltip.js ./src/ContextMenu.js ./src/Panel.js ./src/Utils.js ./src/Init.js > ice_src.js
echo "Compressing ice.js..."
java -jar /usr/local/yuicompressor-2.3.5/build/yuicompressor-2.3.5.jar ice_src.js -o ice.js