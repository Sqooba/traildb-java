
# TrailDB Java [![CircleCI](https://circleci.com/gh/aholyoke/traildb-java/tree/master.svg?style=shield)](https://circleci.com/gh/aholyoke/traildb-java/tree/master)

Java Bindings for [TrailDB](https://github.com/traildb/traildb)

## Installation

`mvn install -P Linux`

or

`mvn install -P Mac`

Only Linux and OS X are supported for the time being. Windows is on the way.

## Run

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
│   │           └── traildb                       ───┐
│   │               ├── TrailDB.java                 │
│   │               ├── TrailDBConstructor.java      │
│   │               ├── TrailDBTrail.java            │
│   │               ├── TrailDBMultiTrail.java       │ 1
│   │               └── TrailDBEventFilter.java      │
│   └── target                                       │
│       └── classes                                  │
│           └── traildb                           <──┘
│               ├── TrailDB.class                 ─────┐
│               ├── TrailDBConstructor.class      ───┐ │
│               ├── TrailDBTrail.class               │ │
│               ├── TrailDBMultiTrail.class          │ │
│               └── TrailDBEventFilter.class         │ │
├── native                                           │ │
│   ├── linux                                      3 │ │ 2
│   │   ├── pom.xml (Linux Build)                    │ │
│   │   └── target                                   │ │
│   │       ├── custom-javah                         │ │
│   │       │   └── traildb-java.h                <──┘ │
│   │       ├── lib                                    │
│   │       │   └── traildbJava.jar               <────┘
│   │       ├── libTraildbJavaNative.so           <──┐
│   │       └── objs                              ───┘ 5
│   │           ├── TrailDB.o                     <──┐
│   │           ├── TrailDBConstructor.o             │
│   │           ├── TrailDBTrail.o                   │
│   │           ├── TrailDBMultiTrail.o              │ 4
│   │           └── TrailDBEventFilter.o             │
│   ├── pom.xml (Native Build)                       │
│   └── src                                          │
│       └── main                                     │
│           └── native                            ───┘
│               ├── TrailDB.c
│               ├── TrailDBConstructor.c
│               ├── TrailDBTrail.c
│               ├── TrailDBMultiTrail.c
│               └── TrailDBEventFilter.c
└── pom.xml (TrailDB)
```

1. Java sources under `java/src/main/java/traildb` are compiled using `javac` to their `.class` files under `java/target/classes/traildb`

2. Java classes are jarred together into `native/<platform>/target/lib/traildbJava.jar`

3. `javah` is used on the `.class` files to automatically generate the header file `native/<platform>/target/custom-javah/traildb-java.h`

4. The header file is included in every c source under `native/src/main/native`. Each c source is compiled with `gcc` to their `.o` object files under `native/<platform>/target/objs`.

5. Each object file is linked into a shared object file `native/<platform>/target/libTraildbJavaNative.so`. The TrailDB library is linked at this stage.


## Images

* The base image is defined in `Dockerfile`. It is an ubuntu image with the jdk and traildb installed. It can be built using `make build`

* The installed image is defined in `Dockerfile.installed`. It is the base image with traildb-java bindings installed. It can be built using `make install`


## Development

- [x] Implement exception raising

- [x] Unit tests

- [x] Set up maven or gradle or something so people can install

- [x] Benchmark relative to other language bindings

- [x] Audit multithreaded support

- [x] Cache field and method lookups

- [x] Benchmark again to evaluate efficacy of field caching

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

- [x] numEvents

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

- [x] close

- [ ] getUUID

- [x] getTrailId

- [x] cursorNew


#### TrailDBTrail

- [x] getTrail

- [x] getTrailLength

- [ ] setEventFilter

- [ ] unsetEventFilter

- [x] next

- [x] peek

- [x] getItem


#### TrailDBMultiTrail

- [ ] setEventFilter

- [ ] unsetEventFilter

- [x] next

- [x] peek

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