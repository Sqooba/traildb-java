CC=gcc
CFLAGS=-shared -fPIC
INCLUDE=-Iinclude -I/usr/lib/jvm/java-8-openjdk-amd64/include -I/usr/lib/jvm/java-8-openjdk-amd64/include/linux
OBJ=src/obj

JAVAS=$(wildcard src/*.java)
EXAMPLES=$(wildcard examples/*.java)
OBJECTS=$(patsubst src/%.java,$(OBJ)/lib%.so,$(JAVAS))
CLASSES=$(patsubst src/%.java,$(OBJ)/%.class,$(JAVAS))

.PHONY: build
build: $(CLASSES) $(OBJECTS)

.PHONY: run
run: build examples
	java -Djava.library.path=$(PWD)/$(OBJ) -classpath '$(OBJ)' Example

debug: build examples
	jdb -Djava.library.path=$(PWD)/$(OBJ) -sourcepath 'examples' -classpath '$(OBJ)' Example

$(OBJ)/lib%.so: src/%.c include/%.h
	$(CC) $(INCLUDE) $(CFLAGS) $< -o $@ -ltraildb

include/%.h:
	javah -jni -classpath '$(OBJ)' -o $@ $(patsubst include/%.h,traildb.%,$@)

$(OBJ)/%.class: src/%.java
	javac $< -d $(OBJ)

.PHONY: examples
examples: $(EXAMPLES)
	javac -classpath '$(OBJ)' $^ -d $(OBJ)

.PHONY: clean
clean:
	rm -f $(OBJ)/*.so $(OBJ)/*.class
	rm include/*.h

.PHONY: descriptors
descriptors:
	javap -s -p $(OBJ)/traildb/*

.PRECIOUS: include/%.h
