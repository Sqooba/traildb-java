#!/bin/bash

jar -xf target/traildb.jar libtraildbjava.dylib
mv libtraildbjava.dylib libtraildbjava-$TRAVIS_TAG.dylib
sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 $SSH_USER@185.19.29.18:/upload <<< $'put 'libtraildbjava-$TRAVIS_TAG'.dylib'
