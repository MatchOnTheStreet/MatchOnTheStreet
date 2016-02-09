#!/bin/bash

dirName="THIS_IS_A_TMP_DIR_NAME"
cd $HOME
mkdir -p $dirName
cd $dirName
git clone https://github.com/MatchOnTheStreet/MatchOnTheStreet.git
cd MatchOnTheStreet
echo "sdk.dir=/Users/larioj/Library/Android/sdk" > local.properties
./gradlew test
cd $HOME
rm -rf $dirName

