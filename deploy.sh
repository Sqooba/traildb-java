#!/bin/bash

sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 -r $SSH_USER@185.19.29.18:/upload down/
jar uf down/traildb.jar down/libtraildbjava.dylib
source versions.properties; package_cloud push Nennya/traildb-java/java down/traildb.jar --coordinates=io.sqooba:traildb:${PROJECT_VERSION}
