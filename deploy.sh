#!/bin/bash

cp .travis.settings.xml $HOME/.m2/settings.xml
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$TRAVIS_TAG
mvn clean deploy -DSkipTests
git add pom.xml
git commit -m "🤖Travis Release"
git push
