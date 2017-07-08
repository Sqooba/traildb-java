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
run: build
	java -Djava.library.path=$(PWD)/$(OBJ) -classpath '$(OBJ)' Sample1 

$(OBJ)/lib%.so: src/%.c include/%.h
	$(CC) $(INCLUDE) $(CFLAGS) $< -o $@

include/%.h:
	javah -classpath 'src' -o $@ $(patsubst include/%.h,%,$@)

$(OBJ)/%.class: src/%.java
	javac $< -d $(OBJ)

.PHONY: examples
examples: $(EXAMPLES)
	javac $^ -d $(OBJ)

.PHONY: clean
clean:
	rm -f $(OBJ)/*.so $(OBJ)/*.class

.PRECIOUS: include/%.h
