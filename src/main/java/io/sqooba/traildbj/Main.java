package io.sqooba.traildbj;

import java.io.IOException;

import io.sqooba.traildbj.TrailDBj.TrailDB;
import io.sqooba.traildbj.TrailDBj.TrailDBConstructor;
public class Main {

    public static void main(String[] args) throws IOException {

        TrailDBConstructor cons = new TrailDBConstructor("test1", new String[] { "field1", "field2", "bob" });
        String cookie = "12345678123456781234567812345678";
        cons.add("2323", 123, new String[] { "a" });
        cons.add(cookie, 124, new String[] { "b", "c" });
        cons.add(cookie, 125, new String[] { "aa", "d" });
        cons.add(cookie, 126, new String[] { "b", "c"});
        cons.add(cookie, 127, new String[] { "b", "c"});
        TrailDB db = cons.finalise();
        cons.close();

        TrailDB db2 = new TrailDB("test1");
        System.out.println(db2.getMinTimestamp());
        //System.out.println(db2.length());
        System.out.println(db2.getLexiconSize(0));
        System.out.println(db2.getLexiconSize(1));
        System.out.println(db2.getLexiconSize(2));
        System.out.println(db2.getLexiconSize(3));
        System.out.println(db2.getLexiconSize(20));
        db2.close();
    }
}
