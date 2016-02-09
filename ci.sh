#!/bin/bash

dirName="THIS_IS_A_TMP_DIR_NAME"
cd $HOME
mkdir -p $dirName
cd $dirName
git clone https://github.com/MatchOnTheStreet/MatchOnTheStreet.git
cd MatchOnTheStreet
./gradlew test
cd $HOME
rm -rf $dirName

