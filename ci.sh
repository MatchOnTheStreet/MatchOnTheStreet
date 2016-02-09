#!/bin/bash

baseDir=$1
tmpDir=$2
sdkLocation=$3

cd $baseDir
mkdir -p $tmpDir
cd $tmpDir
git clone https://github.com/MatchOnTheStreet/MatchOnTheStreet.git
cd MatchOnTheStreet
echo "sdk.dir=$sdkLocation" > local.properties
cat local.properties
./gradlew test
cd $baseDir
rm -rf $tmpDir

