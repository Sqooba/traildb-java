FROM circleci/openjdk

RUN sudo apt-get install -y \
	build-essential \
	libarchive-dev \
	libjudy-dev \
	pkg-config


RUN git clone https://github.com/traildb/traildb.git /home/circleci/traildb && cd /home/circleci/traildb && ./waf configure && sudo ./waf install

ENV LD_LIBRARY_PATH /usr/local/lib
