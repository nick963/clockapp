#!/bin/bash
# Usage: ./makeicns.sh input.png output.icns

set -e

if [ $# -lt 2 ]; then
  echo "Usage: $0 input.png output.icns"
  exit 1
fi

INPUT=$1
OUTPUT=$2
ICONSET="icon.iconset"

# Clean up old iconset if it exists
rm -rf $ICONSET
mkdir $ICONSET

# Generate required sizes
sips -z 16 16     "$INPUT" --out $ICONSET/icon_16x16.png
sips -z 32 32     "$INPUT" --out $ICONSET/icon_16x16@2x.png
sips -z 32 32     "$INPUT" --out $ICONSET/icon_32x32.png
sips -z 64 64     "$INPUT" --out $ICONSET/icon_32x32@2x.png
sips -z 128 128   "$INPUT" --out $ICONSET/icon_128x128.png
sips -z 256 256   "$INPUT" --out $ICONSET/icon_128x128@2x.png
sips -z 256 256   "$INPUT" --out $ICONSET/icon_256x256.png
sips -z 512 512   "$INPUT" --out $ICONSET/icon_256x256@2x.png
sips -z 512 512   "$INPUT" --out $ICONSET/icon_512x512.png
sips -z 1024 1024 "$INPUT" --out $ICONSET/icon_512x512@2x.png

# Convert to .icns
iconutil -c icns $ICONSET -o $OUTPUT

# Cleanup
rm -rf $ICONSET

echo "✅ Created $OUTPUT"
