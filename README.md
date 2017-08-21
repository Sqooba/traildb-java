Java bindings for TrailDB
====

[![Build Status](https://travis-ci.org/Sqooba/traildb-java.svg?branch=master)](https://travis-ci.org/Sqooba/traildb-java)
<a href="https://packagecloud.io/Nennya/traildb-java"><img height="20" alt="Private Maven, RPM, DEB, PyPi and RubyGem Repository | packagecloud" src="https://packagecloud.io/images/packagecloud-badge.png" /></a>

This repository's goal is to provide Java bindings for [TrailDB](http://traildb.io),
 an efficient tool for storing and querying series of events,
 based on the Python bindings available [here](https://github.com/traildb/traildb-python)

## Getting started

This repository requires having TrailDB installed on the machine. See instructions on the
 [TrailDB Github readme](https://github.com/traildb/traildb) and in 
 the [getting started guide](http://traildb.io/docs/getting_started/).

The project is available as a Maven dependency on [packagecloud](https://packagecloud.io/Nennya/traildb-java).
To add it in your pom, you first have to add the following repository:

```
<repositories>
  <repository>
    <id>Nennya-traildb-java</id>
    <url>https://packagecloud.io/Nennya/traildb-java/maven2</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

Then you can add the Maven dependency:

```
<dependency>
  <groupId>io.sqooba</groupId>
  <artifactId>traildb</artifactId>
  <version>1.0.0</version>
</dependency
```

### How to build it

If you want to build from the sources, clone the project from the repository and then launch
 the following Maven command from the project root directory:
 
```
mvn clean install
```

You can also create a fatjar containing all dependencies by running:

```
mvn clean install assembly:single
```

## Minimal examples

### Java

```java
import java.io.IOException;
import java.util.Map;

public class Example {

    public static void main(String args[]) throws IOException {

        // 32-byte hex String.
        // The cookie will be used as the trail's uuid.
        String cookie = "12345678123456781234567812345678";

        // Name of the db, without .tdb.
        String path = "testdb";

        // Building a new TrailDB. Finalization taken care of by .build().
        TrailDB db = new TrailDB.TrailDBBuilder(path, new String[] { "field1", "field2" })
                .add(cookie, 120, new String[] { "a", "b" })
                .add(cookie, 121, new String[] { "c", "d" })
                .build();

        // Iterate over whole db using iterator.
        Map<String, TrailDBIterator> map = db.trails();

        for(Map.Entry<String, TrailDBIterator> entry : map.entrySet()) {
            for(TrailDBEvent event : entry.getValue()) {
                System.out.println(entry.getKey() + " " + event);
            }
        }

        // Iterate over single trail.
        TrailDBIterator trail = db.trail(0);

        for(TrailDBEvent event : trail) {
            System.out.println(event);
        }
    }
}
```

### Scala

Here is an example with the use of the TrailDBBuilder.

```scala
import io.sqooba.traildb.TrailDB
import io.sqooba.traildb.TrailDB.TrailDBBuilder
import io.sqooba.traildb.TrailDBIterator

import scala.collection.JavaConversions._

object BuilderExample {
  def main(args: Array[String]): Unit = {
    val path: String = "testdb";
    val cookie: String = "12345678123456781234567812345678";
    val fields: Array[String] = Array("action", "description");

    // Builder for new TrailDB.
    var builder: TrailDBBuilder = new TrailDB.TrailDBBuilder(path, fields);

    // Add events to builder.
    for (i <- 0 to 10) {
      builder.add(cookie, i + 120, Array("button" + i, "dummy description"));
    }

    // Build the new TrailDB
    val db:TrailDB = builder.build();
    
    // Get iterator over the trail.
    val trail:TrailDBIterator = db.trail(0);
    
    // Print events on the trail.
    for(e <- trail) println(e);
  }
}

```

Other example with directly opening an existing db.

```scala
import io.sqooba.traildb.TrailDB
import io.sqooba.traildb.TrailDBEvent
import io.sqooba.traildb.TrailDBIterator
import scala.collection.JavaConversions._

object ExistingExample {
  def main(args: Array[String]): Unit = {
    // Suppose there exists a testdb.tdb file previously created.
    val path: String = "testdb";

    // Create a TrailDB on an existing tdb file.
    val db: TrailDB = new TrailDB(path);

    // Get iterator over each trail.
    val trails = db.trails();

    // Print all events of all trails.
    for (entry <- trails) {
      for (event: TrailDBEvent <- entry._2) {
        println(entry._1 + " -> " + event);
      }
    }
  }
}
```

## Limitations

### Operating Systems: no Windows

Since TrailDB is only available for Linux and Mac OS's, the same limitation applies here, 
i.e. not working for Windows machines. 

### Architecture: 64 bits only

Furthermore, TrailDB uses 64 bits numbers to encode its items, making the library unusable on 32 bits machines.
