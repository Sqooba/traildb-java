package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildb.TrailDBNative;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;

public class TrailDBNativeTest {

    // This is used to verify that particular messages have been logged when running the tests.
    // Code copied from: http://blog.diabol.se/?p=474
    private static Logger log = Logger.getLogger(TrailDBNative.class.getName());
    private static OutputStream logCapturingStream;
    private static StreamHandler customLogHandler;

    private String getTestCapturedLog() throws IOException {
        customLogHandler.flush();
        return logCapturingStream.toString();
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws IOException {

        // Set up logging capture.
        logCapturingStream = new ByteArrayOutputStream();
        Handler[] handlers = log.getParent().getHandlers();
        customLogHandler = new StreamHandler(logCapturingStream, handlers[0].getFormatter());
        log.addHandler(customLogHandler);
    }

    @Test
    public void constantEnumName() {
        assertEquals(TrailDBNative.INSTANCE, TrailDBNative.valueOf("INSTANCE"));
    }

    @Test
    public void loadLibFail() throws IOException {

        new MockUp<System>() {

            @Mock
            public void exit(int status) {}
        };

        Deencapsulation.invoke(TrailDBNative.INSTANCE, "loadLib", "vi");
        String capturedLog = getTestCapturedLog();
        assertTrue(capturedLog.contains("Failed to load library."));
    }

    @Test
    public void errorStrShouldReturnCorrectMessage() {
        assertEquals("TDB_ERR_INVALID_TRAIL_ID", TrailDBNative.INSTANCE.errorStr(-6));
    }
}
