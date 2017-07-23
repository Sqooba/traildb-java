
deploy:
	docker build -t traildb-java .
	docker tag traildb-java aholyoke/traildb-java:latest
	sudo docker push aholyoke/traildb-java
