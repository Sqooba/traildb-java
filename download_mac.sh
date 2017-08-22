#!/bin/bash

sudo apt-get install -qq sshpass
gem install package_cloud
sshpass -p $SSH_PASS sftp -oStrictHostKeyChecking=no -oPort=2223 -r $SSH_USER@185.19.29.18:/upload down/
