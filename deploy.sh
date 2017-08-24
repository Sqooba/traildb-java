#!/bin/bash

./download_mac.sh
mvn clean install -DskipTests
cp .travis.settings.xml $HOME/.m2/settings.xml
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$TRAVIS_TAG
mvn clean deploy -DSkipTests
