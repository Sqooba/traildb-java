CC=gcc
CFLAGS=-shared -fPIC
INCLUDE=-Iinclude -I/usr/lib/jvm/java-8-openjdk-amd64/include -I/usr/lib/jvm/java-8-openjdk-amd64/include/linux
TGT=target

JSOURCE=$(wildcard src/*.java)
CSOURCE=$(wildcard src/*.c)
EXAMPLES=$(wildcard examples/*.java)
LIBRARIES=$(patsubst src/%.c,$(TGT)/lib%.so,$(CSOURCE))
JCLASSES=$(patsubst src/%.java,$(TGT)/%.class,$(JSOURCE))

.PHONY: build
build: $(TGT)/TrailDB.class $(LIBRARIES)

.PHONY: run
run: build examples
	java -Djava.library.path=$(PWD)/$(TGT) -classpath '$(TGT)' Example

debug: build examples
	jdb -Djava.library.path=$(PWD)/$(TGT) -sourcepath 'examples' -classpath '$(TGT)' Example

# LIBRARIES
$(TGT)/lib%.so: src/%.c include/%.h
	$(CC) $(INCLUDE) $(CFLAGS) $< -o $@ -ltraildb

include/%.h:
	javah -jni -classpath '$(TGT)' -o $@ $(patsubst include/%.h,traildb.%,$@)

# JCLASSES
$(TGT)/%.class: src/%.java
	javac $(JSOURCE) -d $(TGT)

.PHONY: examples
examples: $(EXAMPLES)
	javac -classpath '$(TGT)' $^ -d $(TGT)

.PHONY: clean
clean:
	rm -f $(TGT)/*.so $(TGT)/traildb/*.class
	rm include/*.h

.PHONY: descriptors
descriptors:
	javap -s -p $(TGT)/traildb/*

.PRECIOUS: include/%.h
