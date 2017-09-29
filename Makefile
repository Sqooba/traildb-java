
.PHONY: all
all: deploy_base deploy_installed

.PHONY: build
build:
	docker build -t traildb-java .

.PHONY: install
install:
	docker build -t traildb-java -f Dockerfile.installed .

.PHONY: run
run:
	docker run -it traildb-java bash

.PHONY: deploy_base
deploy_base: build
	docker tag traildb-java aholyoke/traildb-java:base
	docker push aholyoke/traildb-java

.PHONY: deploy_installed
deploy_installed: install
	docker tag traildb-java aholyoke/traildb-java:installed
	docker push aholyoke/traildb-java
