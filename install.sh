#!/bin/bash

if [[ $TRAVIS_OS_NAME == 'osx' ]]; then

	brew install traildb;

else
	sudo apt-get update -qq
	sudo apt-get install -qq libarchive-dev libjudy-dev pkg-config build-essential

	wget https://mirrors.kernel.org/ubuntu/pool/universe/j/judy/libjudy-dev_1.0.5-5_amd64.deb
	wget https://mirrors.kernel.org/ubuntu/pool/universe/j/judy/libjudydebian1_1.0.5-5_amd64.deb
	sudo dpkg -i libjudy-dev_1.0.5-5_amd64.deb libjudydebian1_1.0.5-5_amd64.deb

	cd ..
 	git clone https://github.com/traildb/traildb
	cd traildb
	./waf configure
	./waf build
	sudo ./waf install
	./waf test
  
	cd ..
	cd traildb-java

	sudo ldconfig
	LD_LIBRARY_PATH=/lib:/usr/lib:/usr/local/lib
fi
