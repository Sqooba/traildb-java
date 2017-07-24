
# TrailDB Java [![CircleCI](https://circleci.com/gh/aholyoke/traildb-java/tree/master.svg?style=shield)](https://circleci.com/gh/aholyoke/traildb-java/tree/master)

Java Bindings for [TrailDB](https://github.com/traildb/traildb)

## Installation

`mvn install -P Linux`

Only Linux is supported for the time being. OS X and Windows are on the way.

Compile and run the example by adding the jar to your classpath and setting `java.library.path` to the object file.

```
javac -cp native/linux/target/lib/traildbJava.jar examples/Example.java

java -Djava.library.path=`pwd`/native/linux/target/ -cp examples:native/linux/target/lib/traildbJava.jar Example
```

## Architecture

```
├── java
│   ├── pom.xml (TrailDB Java Classes)
│   ├── src
│   │   └── main
│   │       └── java
│   │           └── traildb
│   │               ├── TrailDBConstructor.java
│   │               ├── TrailDBCursor.java
│   │               ├── TrailDBEventFilter.java
│   │               ├── TrailDBEvent.java
│   │               ├── TrailDBItem.java
│   │               ├── TrailDB.java
│   │               ├── TrailDBMultiCursor.java
│   │               └── TrailDBMultiEvent.java
│   └── target
│        └── classes
│        	└── traildb
│        		├── TrailDB.class
│   			├── TrailDBConstructor.class
│   			├── TrailDBConstructor$TDB_OPT_CONS_KEY.class
│   			├── TrailDBConstructor$TDB_OPT_CONS_VALUE.class
│   			├── TrailDBCursor.class
│   			├── TrailDBEvent.class
│   			├── TrailDBEventFilter.class
│   			├── TrailDBItem.class
│   			├── TrailDBMultiCursor.class
│   			├── TrailDBMultiEvent.class
│   			├── TrailDB$TDB_OPT_KEY.class
│   			└── TrailDB$TDB_OPT_VALUE.class
├── native
│   ├── linux
│   │   ├── pom.xml (Linux Build)
│   │   └── target
│   │       ├── custom-javah
│   │       │   └── traildb-java.h
│   │       ├── lib
│   │       │   └── traildbJava.jar
│   │       ├── libTraildbJavaNative.so
│   │       └── objs
│   │           ├── TrailDBConstructor.o
│   │           ├── TrailDBCursor.o
│   │           ├── TrailDBEventFilter.o
│   │           ├── TrailDBEvent.o
│   │           ├── TrailDBItem.o
│   │           ├── TrailDBMultiCursor.o
│   │           └── TrailDB.o
│   ├── pom.xml (Native Build)
│   └── src
│       └── main
│           └── native
│               ├── TrailDB.c
│               ├── TrailDBConstructor.c
│               ├── TrailDBCursor.c
│               ├── TrailDBEvent.c
│               ├── TrailDBEventFilter.c
│               ├── TrailDBItem.c
│               └── TrailDBMultiCursor.c
└── pom.xml (TrailDB)
```

1. Java sources under `java/src/main/java/traildb` are compiled using `javac` to their `.class` files under `java/target/classes/traildb`

2. Java classes are jarred together into `native/<arch>/target/lib/traildbJava.jar`

3. `javah` is used on the `.class` files to automatically generate the header file `native/<arch>/target/custom-javah/traildb-java.h`

4. The header file is included in every c source under `native/src/main/native`. Each c source is compiled with `gcc` to their `.o` object files under `native/<arch>/target/objs`.

5. Each object file is linked into a shared object file `native/<arch>/target/libTraildbJavaNative.so`. The TrailDB library is linked at this stage.


## Deployment

Tests are run inside a docker container which can be found at `aholyoke/traildb-java:latest`. This image already has TrailDB installed. `make deploy` can be used to redeploy the image to Docker Hub. CircleCI will use this image to build and run tests.


## TODO

- [x] Implement exception raising

- [x] Unit tests

- [x] Set up maven or gradle or something so people can install

- [x] Benchmark relative to other language bindings

- [ ] Audit multithreaded support

- [ ] Cache field and method lookups

- [ ] Benchmark again to evaluate efficacy of field caching

- [ ] Finish implementing everything else

- [ ] Enable -Wall and -Werr and fix issues

### Functions


#### TrailDBConstructor

- [x] Constructor

- [x] add

- [x] append

- [x] finalize

- [x] close

- [x] setOpt

- [x] getOpt


#### TrailDB

- [x] Constructor

- [ ] dontNeed

- [ ] willNeed

- [x] numTrails

- [ ] numEvents

- [x] numFields

- [ ] minTimestamp

- [ ] maxTimestamp

- [ ] version

- [ ] setOpt

- [ ] getOpt

- [ ] setTrailOpt

- [ ] getTrailOpt

- [ ] lexiconSize

- [ ] getField

- [x] getFieldName

- [ ] getItem

- [ ] close

- [ ] getUUID

- [ ] getTrailId

- [x] cursorNew

- [ ] multiCursorNew


#### TrailDBCursor

- [ ] free

- [x] getTrail

- [ ] getTrailLength

- [ ] setEventFilter

- [ ] unsetEventFilter

- [x] next

- [x] peek


#### TrailDBEvent

- [x] getItem


#### TrailDBEventFilter

- [ ] free

- [ ] addTerm

- [ ] addTimeRange

- [ ] newClause

- [ ] numClauses

- [ ] numTerms

- [ ] isNegative

- [ ] getItem

- [ ] getStartTime

- [ ] getEndTime


#### TrailDBMultiCursor

- [ ] free

- [ ] reset

- [ ] next

- [ ] nextBatch

- [ ] peek
