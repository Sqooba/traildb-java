# Java bindings for TrailDB #

This repository's goal is to provide Java bindings for TrailDB, based on the Python bindings available [here](https://github.com/traildb/traildb-python)

### Getting started ###

This repository requires having TrailDB installed on the machine. See instructions on the [TrailDB Github readme](https://github.com/traildb/traildb) and in the [getting started guide](http://traildb.io/docs/getting_started/).

At the moment the project is not available but will be soon put in the Sqooba Maven Repository and be available as a single Maven dependency to add to your project.

### Minimal example ###

```java
import io.sqooba.traildbj.TrailDBj.TrailDB;
import io.sqooba.traildbj.TrailDBj.TrailDBConstructor;

public class Example {

    public static void main(String args[]) throws IOException {
    
        // 32-byte hex String.
        String cookie = "12345678123456781234567812345678";
        
        // Name of the db, without .tdb.
        String path = "testdb";
    
        // Construct a TrailDB.
        TrailDBConstructor cons = new TrailDBConstructor(path, new String[] { "field1", "field2" });
        
        // Adding events.
        cons.add(cookie, 120, new String[] { "a", "b" });
        cons.add(cookie, 121, new String[] { "c", "d" });
        
        // Writing db to a file.
        TrailDB db = cons.finalise();
        
        // Releasing cons handle.
        cons.close();
    }
}
```

### Binded methods ###

The full list of methods can be found on the [C API web page](http://traildb.io/docs/api/)

#### Construct a new TrailDB ####

| Method            | Binded | Exposed |
|-------------------|--------|---------|
| tdb_cons_init     | Yes    | No      |
| tdb_cons_open     | Yes    | No      |
| tdb_cons_close    | Yes    | Yes     |
| tdb_cons_add      | Yes    | Yes     |
| tdb_cons_set_opt  | No     | No      |
| tdb_cons_get_opt  | No     | No      |
| tdb_cons_finalize | Yes    | Yes     |

#### Open a TrailDB and access metadata ####

| Method            | Binded | Exposed |
|-------------------|--------|---------|
| tdb_init          | Yes    | No      |
| tdb_open          | Yes    | No      |
| tdb_close         | Yes    | No      |
| tdb_dontneed      | No     | No      |
| tdb_willneed      | No     | No      |
| tdb_num_trails    | Yes    | Yes     |
| tdb_num_events    | Yes    | No      |
| tdb_num_fields    | Yes    | No      |
| tdb_min_timestamp | Yes    | Yes     |
| tdb_max_timestamp | Yes    | Yes     |
| tdb_version       | Yes    | Yes     |
| tdb_error_str     | Yes     | No      |

#### Setting Options ####

Nothing.

#### Working with items, fields and values ####

| Method             | Binded | Exposed |
|--------------------|--------|---------|
| tdb_item_field     | No     | No      |
| tdb_item_val       | No     | No      |
| tdb_make_item      | No     | No      |
| tdb_item_is32      | No     | No      |
| tdb_lexicon_size   | Yes    | Yes     |
| tdb_get_field      | Yes    | Yes     |
| tdb_get_field_name | Yes    | Yes     |
| tdb_get_item       | Yes    | Yes     |
| tdb_get_value      | Yes    | Yes     |
| tdb_get_item_value | Yes    | Yes     |

#### Working with UUIDs ####

| Method           | Binded | Exposed |
|------------------|--------|---------|
| tdb_get_uuid     | Yes    | Yes     |
| tdb_get_trail_id | Yes    | Yes     |
| tdb_uuid_raw     | Yes    | Yes     |
| tdb_uuid_hex     | Yes    | Yes     |

#### More to come in the future ####
