CC=gcc
CFLAGS=-shared -fPIC
INCLUDE=-Iinclude -I/usr/lib/jvm/java-8-openjdk-amd64/include -I/usr/lib/jvm/java-8-openjdk-amd64/include/linux
OBJ=src/obj

.PHONY: build
build: $(OBJ)/libSample1.so $(OBJ)/Sample1.class

.PHONY: run
run: build
	java -Djava.library.path=$(PWD)/$(OBJ) -classpath '$(OBJ)' Sample1

$(OBJ)/libSample1.so: src/Sample1.c include/Sample1.h 
	$(CC) $(INCLUDE) $(CFLAGS) $< -o $@

$(OBJ)/Sample1.class: src/Sample1.java
	javac src/Sample1.java -d $(OBJ)


.PHONY: clean
clean:
	rm -f $(OBJ)/*.so $(OBJ)/*.class
