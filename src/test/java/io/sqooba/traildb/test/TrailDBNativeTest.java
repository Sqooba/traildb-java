package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @After
    public void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    public void constantEnumName() {
        assertEquals(TrailDBNative.INSTANCE, TrailDBNative.valueOf("INSTANCE"));
    }

    @Test
    public void loadLibFail() {

        new MockUp<System>() {

            @Mock
            public void exit(int status) {}
        };

        Deencapsulation.invoke(TrailDBNative.INSTANCE, "loadLib", "vi");
        assertTrue(this.logger.getLoggingEvents().stream()
                .anyMatch(e -> "Failed to load library.".equals(e.getMessage())));
    }

    @Test
    public void errorStrShouldReturnCorrectMessage() {
        assertEquals("TDB_ERR_INVALID_TRAIL_ID", TrailDBNative.INSTANCE.errorStr(-6));
    }
}
