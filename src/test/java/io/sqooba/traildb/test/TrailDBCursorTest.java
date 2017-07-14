package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDBCursor;
import io.sqooba.traildb.TrailDBNative;
import mockit.Deencapsulation;

public class TrailDBCursorTest {

    private TrailDB db;
    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";

    @Before
    public void setUp() {
        this.db = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" })
                .add(this.cookie, 120, new String[] { "a", "hinata" })
                .build();
    }

    @Test
    public void closeWithNullCursorField() {

        TrailDBCursor cursor = this.db.trail(0);

        Deencapsulation.setField(cursor, "cursor", null);
        cursor.close();
    }

    @Test
    public void getTrailLengthShouldReturnCorrectRemainingEvents() {
        TrailDBCursor cursor = this.db.trail(0);
        assertEquals(1, TrailDBNative.INSTANCE.getTrailLength(Deencapsulation.getField(cursor, "cursor")));
        cursor.close();
    }

    @Test
    public void closeShouldFreeCursor() {
        TrailDBCursor cursor = this.db.trail(0);
        cursor.close();
        assertNull(Deencapsulation.getField(cursor, "cursor"));
    }

}
