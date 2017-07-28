
all: build deploy

.PHONY: build
build:
	docker build -t traildb-java .

.PHONY: install
install:
	docker build -t traildb-java -f Dockerfile.installed .

.PHONY: run
run:
	docker run -it traildb-java bash

.PHONY: deploy
deploy:
	docker tag traildb-java aholyoke/traildb-java:latest
	sudo docker push aholyoke/traildb-java
