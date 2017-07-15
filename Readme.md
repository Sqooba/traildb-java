
# TrailDB Java

Java Bindings for [TrailDB](https://github.com/traildb/traildb)

## Installation

`mvn install -P Linux`

Only Linux is supported for the time being. OS X and Windows are on the way.

Compile and run the example by adding the jar to your classpath and setting `java.library.path` to the object file.

```
javac -cp native/linux/target/lib/traildbJava.jar examples/Example.java

java -Djava.library.path=`pwd`/native/linux/target/ -cp examples:native/linux/target/lib/traildbJava.jar Example
```


## TODO

- [x] Implement exception raising

- [ ] Unit tests

- [x] Set up maven or gradle or something so people can install

- [ ] Benchmark relative to other language bindings

- [ ] Cache field and method lookups

- [ ] Benchmark again to evaluate efficacy of field caching

- [ ] Finish implementing everything else

- [ ] Enable -Wall and -Werr and fix issues

### Functions


#### TrailDBConstructor

- [x] Constructor

- [x] add

- [ ] append

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

- [ ] peek


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
