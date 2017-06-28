package io.sqooba.traildbj;

import java.io.IOException;

import io.sqooba.traildbj.TrailDBj.TrailDBConstructor;

public class Main {

    public static void main(String[] args) throws IOException {

        TrailDBConstructor cons = new TrailDBConstructor("test1", new String[] { "field1", "field2" });
        String cookie = "12345678123456781234567812345678";
        cons.add(cookie, 123, new String[] { "a" });
        cons.add(cookie, 124, new String[] { "b", "c" });
        cons.finalize();
        cons.close();
    }
}
