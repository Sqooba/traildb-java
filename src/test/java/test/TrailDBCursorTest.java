package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.sqooba.traildbj.TrailDB;
import io.sqooba.traildbj.TrailDBConstructor;
import io.sqooba.traildbj.TrailDBCursor;
import io.sqooba.traildbj.TrailDBj;
import mockit.Deencapsulation;
import mockit.Expectations;

public class TrailDBCursorTest {

    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";

    @Test
    public void cursorFinalizationWithNullCursorField() {
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.add(this.cookie, 120, new String[] { "a", "hinata" });
        TrailDB db = cons.finalise();
        TrailDBCursor cursor = db.trail(0);

        new Expectations(cursor) {

            {
                Deencapsulation.setField(cursor, "cursor", null);
            }
        };
    }

    @Test
    public void getTrailLengthShouldReturnCorrectRemainingEvents() {
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.add(this.cookie, 120, new String[] { "a", "hinata" });
        TrailDB db = cons.finalise();
        TrailDBCursor cursor = db.trail(0);
        assertEquals(1, TrailDBj.INSTANCE.getTrailLength(Deencapsulation.getField(cursor, "cursor")));
    }

}
