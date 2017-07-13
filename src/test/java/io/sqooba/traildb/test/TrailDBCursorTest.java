package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDBCursor;
import io.sqooba.traildb.TrailDBNative;
import mockit.Deencapsulation;
import mockit.Expectations;

public class TrailDBCursorTest {

    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";

    @Test
    public void cursorFinalizationWithNullCursorField() {
        TrailDB db = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" })
                .add(this.cookie, 120, new String[] { "a", "hinata" })
                .build();
        TrailDBCursor cursor = db.trail(0);

        new Expectations(cursor) {

            {
                Deencapsulation.setField(cursor, "cursor", null);
            }
        };
    }

    @Test
    public void getTrailLengthShouldReturnCorrectRemainingEvents() {
        TrailDB db = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" })
                .add(this.cookie, 120, new String[] { "a", "hinata" })
                .build();
        TrailDBCursor cursor = db.trail(0);
        assertEquals(1, TrailDBNative.INSTANCE.getTrailLength(Deencapsulation.getField(cursor, "cursor")));
    }

}
