#!/bin/bash

sudo apt-get install -qq sshpass
gem install package_cloud

# Download the mac library.
sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 -r $SSH_USER@185.19.29.18:/upload down/
# Cleanup.
sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 $SSH_USER@185.19.29.18:/upload <<< "rm libtraildbjava-$TRAVIS_TAG.dylib"
# Rename so it can be used by maven.
mv libtraildbjava-$TRAVIS_TAG.dylib libtraildbjava.dylib
