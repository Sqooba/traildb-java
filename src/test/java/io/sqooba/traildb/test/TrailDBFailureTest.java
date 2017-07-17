package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDB.TrailDBBuilder;
import io.sqooba.traildb.TrailDBError;
import io.sqooba.traildb.TrailDBNative;
import mockit.Expectations;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class TrailDBFailureTest {

    private TrailDB db;
    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";
    private String otherCookie = "12121212121212121212121212121212";

    private final TestLogger logger = TestLoggerFactory.getTestLogger(TrailDB.class);

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws IOException {

        // Initialise a TrailDB with some TrailDBEvents.
        TrailDBBuilder builder = new TrailDBBuilder(this.path, new String[] { "field1", "field2" });
        builder.add(this.cookie, 120, new String[] { "a", "hinata" });
        builder.add(this.cookie, 121, new String[] { "vilya", "" });
        builder.add(this.otherCookie, 122, new String[] { "kaguya", "hinata" });
        builder.add(this.otherCookie, 123, new String[] { "alongstring", "averyveryverylongstring" });
        this.db = builder.build();
    }

    @After
    public void tearDown() throws IOException {

        // Clear the TrailDB files/directories created for the tests.
        File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
        TestLoggerFactory.clear();
    }

    @Test
    public void openFailure() {
        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to open db.");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.open((ByteBuffer)this.any, this.anyString);
                this.result = -1;
            }
        };
        try (TrailDB db = new TrailDB(this.path)) {
            // Auto close.
        }
    }

    @Test
    public void minTimestampOverflow() throws IOException {
        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.minTimestamp((ByteBuffer)this.any);
                this.result = -1;
            }
        };
        this.db.getMinTimestamp();
        assertEquals(this.logger.getLoggingEvents().get(0).getMessage(),
                "long overflow, received a negtive value for min timestamp.");
    }

    @Test
    public void maxTimestampOverflow() throws IOException {
        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.maxTimestamp((ByteBuffer)this.any);
                this.result = -1;
            }
        };
        this.db.getMaxTimestamp();
        assertEquals(this.logger.getLoggingEvents().get(0).getMessage(),
                "long overflow, received a negtive value for max timestamp.");
    }

    @Test
    public void versionOverflow() throws IOException {
        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.version((ByteBuffer)this.any);
                this.result = -1;
            }
        };
        this.db.getVersion();
        assertEquals(this.logger.getLoggingEvents().get(0).getMessage(), "version overflow.");
    }

    @Test
    public void getItemFailure() throws IOException {

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.getItem((ByteBuffer)this.any, this.anyLong, this.anyString);
                this.result = -1;
            }
        };

        this.db.getItem(0, "bla");
        assertEquals(this.logger.getLoggingEvents().get(0).getMessage(),
                "Returned item overflow, deal with it carefully!");
    }

    @Test
    public void getUUIDFailure() {
        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Invalid trail ID.");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.getUUID((ByteBuffer)this.any, this.anyLong);
                this.result = null;
            }
        };
        this.db.getUUID(1);
    }

    @Test
    public void trailCursorNewFailure() {
        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Memory allocation failed for cursor.");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.cursorNew((ByteBuffer)this.any);
                this.result = null;
            }
        };
        this.db.trail(0);
    }

    @Test
    public void trailFailure() {
        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to create cursor with code: -1");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.getTrail((ByteBuffer)this.any, this.anyLong);
                this.result = -1;
            }
        };
        this.db.trail(0);
    }
}
