package test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import io.sqooba.traildbj.TrailDBj.TrailDB;
import io.sqooba.traildbj.TrailDBj.TrailDBConstructor;

public class Test2 {

    private TrailDB db;
    private String path = "testdb";

    @Test
    public void setUp() throws IOException {
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        String cookie = "12345678123456781234567812345678";
        cons.add(cookie, 120, new String[] { "a", "b" });
        cons.add(cookie, 121, new String[] { "vilya", "b" });
        cons.add(cookie, 122, new String[] { "kaguya", "hinata" });
        cons.add(cookie, 123, new String[] { "aaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbb" });
        this.db = cons.finalise();
        cons.close();
    }

    @After
    public void tearDown() throws IOException {
        File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
    }
}
