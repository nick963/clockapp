#!/bin/bash

# ==============================================================================
# Mac App Packaging Script
# This script automates the process of creating a native macOS application bundle
# (.app) from a Java JAR file using the jpackage tool.
#
# Prerequisites:
# - JDK 14 or later with jpackage in the PATH.
# - Your project must be a Maven project.
# - The final JAR and any dependencies must be located in a 'dist' directory.
#
# Usage:
#   1. Make the script executable: chmod +x package-mac-app.sh
#   2. Run the script from the project root: ./package-mac-app.sh
# ==============================================================================

cd ..
echo "--- Starting the build and packaging process ---"

# --- 1. Clean and build the Maven project ---
# This step ensures you have the latest executable JAR.
echo "Running Maven clean and package..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Maven build failed. Aborting."
    exit 1
fi

echo "Maven build successful."

# --- 2. Prepare the distribution directory ---
# Create a 'dist' directory and copy the main JAR into it.
# This assumes the JAR is in the 'target' directory after a successful build.
echo "Preparing 'dist' directory..."
rm -rf dist
mkdir dist
cp target/clockapp-1.0-SNAPSHOT.jar dist/

if [ $? -ne 0 ]; then
    echo "Failed to copy JAR to dist directory. Aborting."
    exit 1
fi

# --- 3. Define variables for jpackage command ---
APP_NAME="Clock App"
MAIN_JAR="clockapp-1.0-SNAPSHOT.jar"
MAIN_CLASS="org.clock.ClockApp"
VENDOR_NAME="Nick Lerissa"
MAC_PACKAGE_ID="org.clock"

# --- 4. Run the jpackage command ---
echo "Running jpackage to create the .app bundle..."
jpackage \
  --input dist \
  --name "$APP_NAME" \
  --main-jar "$MAIN_JAR" \
  --main-class "$MAIN_CLASS" \
  --type app-image \
  --vendor "$VENDOR_NAME" \
  --mac-package-identifier "$MAC_PACKAGE_ID"

if [ $? -ne 0 ]; then
    echo "jpackage failed. Aborting."
    exit 1
fi

echo "--- Packaging complete! ---"
echo "Your application can be found at: $APP_NAME.app"
