#!/bin/bash

sudo apt-get install -qq sshpass
gem install package_cloud

# Download the mac library.
sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 $SSH_USER@185.19.29.18:/upload <<< "get libtraildbjava.dylib-$TRAVIS_TAG"
# Cleanup.
sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 $SSH_USER@185.19.29.18:/upload <<< "rm libtraildbjava.dylib-$TRAVIS_TAG"
# Rename so it can be used by maven.
mv libtraildbjava.dylib-$TRAVIS_TAG libtraildbjava.dylib
