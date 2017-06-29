package io.sqooba.traildbj;

import java.io.IOException;

import io.sqooba.traildbj.TrailDBj.TrailDB;
import io.sqooba.traildbj.TrailDBj.TrailDBConstructor;
public class Main {

    public static void main(String[] args) throws IOException {

        TrailDBConstructor cons = new TrailDBConstructor("test1", new String[] { "field1", "field2" });
        String cookie = "12345678123456781234567812345678";
        cons.add(cookie, 123, new String[] { "a" });
        cons.add(cookie, 124, new String[] { "b", "c" });
        TrailDB db = cons.finalise();
        cons.close();

        TrailDB db2 = new TrailDB("test1");
        System.out.println(db2.getMinTimestamp());
        System.out.println(db.length());
        db2.close();
    }
}
