package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDBIterator;
import io.sqooba.traildb.TrailDBNative;
import mockit.Deencapsulation;

public class TrailDBIteratorTest {

    private TrailDB db;
    private final String path = "testdb";
    private final String cookie = "12345678123456781234567812345678";

    @Before
    public void setUp() {
        this.db = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" })
                .add(this.cookie, 120, new String[] { "a", "hinata" })
                .build();
    }

    @After
    public void tearDown() throws IOException {

        // Clear the TrailDB files/directories created for the tests.
        final File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
    }

    @Test
    public void closeWithNullCursorField() {

        final TrailDBIterator cursor = this.db.trail(0);

        Deencapsulation.setField(cursor, "cursor", -1);
        cursor.close();
    }

    @Test
    public void getTrailLengthShouldReturnCorrectRemainingEvents() {
        final TrailDBIterator cursor = this.db.trail(0);
        assertEquals(1, TrailDBNative.INSTANCE.getTrailLength(Deencapsulation.getField(cursor, "cursor")));
        cursor.close();
    }

    @Test
    public void closeShouldFreeCursor() {
        final TrailDBIterator cursor = this.db.trail(0);
        cursor.close();
        assertEquals(-1, (long)Deencapsulation.getField(cursor, "cursor"));
    }

    @Test
    public void iteratorsShoudNotInterfere() {
        final TrailDB db = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" })
                .add(this.cookie, 120, new String[] { "a", "hinata" })
                .build();

        final TrailDBIterator trail1 = db.trail(0);
        final TrailDBIterator trail2 = db.trail(0);

        assertEquals(trail1.iterator().next().toString(), trail2.iterator().next().toString());
    }

}
