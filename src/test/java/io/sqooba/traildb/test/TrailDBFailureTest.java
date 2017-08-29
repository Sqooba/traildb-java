package io.sqooba.traildb.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDB.TrailDBBuilder;
import io.sqooba.traildb.TrailDBException;
import io.sqooba.traildb.TrailDBNative;
import mockit.Expectations;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class TrailDBFailureTest {

    private final TrailDBNative traildb = TrailDBNative.INSTANCE;

    private TrailDB db;
    private final String path = "testdb";
    private final String cookie = "12345678123456781234567812345678";
    private final String otherCookie = "12121212121212121212121212121212";

    private final TestLogger logger = TestLoggerFactory.getTestLogger(TrailDB.class);

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws IOException {

        // Initialise a TrailDB with some TrailDBEvents.
        final TrailDBBuilder builder = new TrailDBBuilder(this.path, new String[] { "field1", "field2" });
        builder.add(this.cookie, 120, new String[] { "a", "hinata" });
        builder.add(this.cookie, 121, new String[] { "vilya", "" });
        builder.add(this.otherCookie, 122, new String[] { "kaguya", "hinata" });
        builder.add(this.otherCookie, 123, new String[] { "alongstring", "averyveryverylongstring" });
        this.db = builder.build();
    }

    @After
    public void tearDown() throws IOException {

        // Clear the TrailDB files/directories created for the tests.
        final File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
        TestLoggerFactory.clear();
    }

    @Test
    public void initFailure() {
        this.expectedEx.expect(TrailDBException.class);
        this.expectedEx.expectMessage("Failed to allocate memory to init a new TrailDB.");

        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.init();
                this.result = -1;
            }
        };
        try (TrailDB db = new TrailDB(this.path)) {
            // Auto close.
        }
    }

    @Test
    public void openFailure() {
        this.expectedEx.expect(TrailDBException.class);
        this.expectedEx.expectMessage("Failed to open db.");

        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.open(this.anyLong, this.anyString);
                this.result = -1;
            }
        };
        try (TrailDB db = new TrailDB(this.path)) {
            // Auto close.
        }
    }

    @Test
    public void minTimestampOverflow() throws IOException {
        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.minTimestamp(this.anyLong);
                this.result = -1;
            }
        };
        this.db.getMinTimestamp();
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "long overflow, received a negtive value for min timestamp.".equals(e.getMessage())));
    }

    @Test
    public void maxTimestampOverflow() throws IOException {
        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.maxTimestamp(this.anyLong);
                this.result = -1;
            }
        };
        this.db.getMaxTimestamp();
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "long overflow, received a negative value for max timestamp.".equals(e.getMessage())));
    }

    @Test
    public void versionOverflow() throws IOException {
        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.version(this.anyLong);
                this.result = -1;
            }
        };
        this.db.getVersion();
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "version overflow.".equals(e.getMessage())));
    }

    @Test
    public void getItemFailure() throws IOException {

        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.getItem(this.anyLong, this.anyLong, this.anyString);
                this.result = -1;
            }
        };

        this.db.getItem(0, "bla");
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "Returned item overflow, deal with it carefully!".equals(e.getMessage())));
    }

    @Test
    public void getUUIDFailure() {
        this.expectedEx.expect(TrailDBException.class);
        this.expectedEx.expectMessage("Invalid trail ID.");

        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.getUUID(this.anyLong, this.anyLong);
                this.result = null;
            }
        };
        this.db.getUUID(1);
    }

    @Test
    public void trailCursorNewFailure() {
        this.expectedEx.expect(TrailDBException.class);
        this.expectedEx.expectMessage("Memory allocation failed for cursor.");

        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.cursorNew(this.anyLong);
                this.result = -1;
            }
        };
        this.db.trail(0);
    }

    @Test
    public void trailFailure() {
        this.expectedEx.expect(TrailDBException.class);
        this.expectedEx.expectMessage("Failed to create cursor with code: -1");

        final TrailDBNative traildbj = this.traildb;
        new Expectations(traildbj) {

            {
                traildbj.getTrail(this.anyLong, this.anyLong);
                this.result = -1;
            }
        };
        this.db.trail(0);
    }
}
