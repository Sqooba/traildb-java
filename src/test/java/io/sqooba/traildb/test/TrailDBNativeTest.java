package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildb.TrailDBNative;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class TrailDBNativeTest {

    private final TestLogger logger = TestLoggerFactory.getTestLogger(TrailDBNative.class);

    private TrailDBNative traildb = TrailDBNative.INSTANCE;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @After
    public void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void constantEnumName() {
        assertEquals(this.traildb, TrailDBNative.valueOf("INSTANCE"));
    }

    @Test
    public void loadLibFailWithExceptionNullFileOut() {

        new MockUp<System>() {

            @Mock
            public void exit(int status) {}
        };

        new MockUp<File>() {

            @Mock
            public File createTempFile(String prefix, String suffix, File directory) throws IOException {
                throw new IOException();
            }
        };

        Deencapsulation.invoke(this.traildb, "loadLib", "traildbjava");
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "Failed to load library.".equals(e.getMessage())));
    }

    @Test
    public void loadLibFailWithExceptionNotNullFileOut() {

        new MockUp<System>() {

            @Mock
            public void exit(int status) {}
        };

        new MockUp<FileUtils>() {

            @Mock
            public FileOutputStream openOutputStream(File file) throws IOException {
                throw new IOException();
            }
        };

        Deencapsulation.invoke(this.traildb, "loadLib", "traildbjava");
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "Failed to load library.".equals(e.getMessage())));
    }

    @Test
    public void errorStrShouldReturnCorrectMessage() {
        assertEquals("TDB_ERR_INVALID_TRAIL_ID", this.traildb.errorStr(-6));
    }
}
