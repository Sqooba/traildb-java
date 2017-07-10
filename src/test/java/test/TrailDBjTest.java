package test;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import io.sqooba.traildbj.TrailDBConstructor;
import io.sqooba.traildbj.TrailDBError;
import io.sqooba.traildbj.TrailDBj;

/**
 * These are some tests to show how to mock C lib calls, but each test is a pain to write because spying a singleton
 * enum class does not work properly.
 * 
 * @author Vilya
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TrailDBj.class })
public class TrailDBjTest {

    private TrailDBj mockInstance;
    private byte[] someBytes = new byte[8];
    private ByteBuffer someBB = ByteBuffer.allocate(8);

    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() {
        this.mockInstance = PowerMockito.mock(TrailDBj.class);
        Whitebox.setInternalState(TrailDBj.class, "INSTANCE", this.mockInstance);
    }

    @After
    public void tearDown() throws IOException {
        // Clear the TrailDB files/directories created for the tests.
        File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
    }

    @Test
    public void constructionFailure() throws Exception {

        // Test that the correct exception is thrown when the initial db construction fails (C lib failure).

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to allocate memory for constructor.");

        // Mock the init C call to make it fail.
        PowerMockito.when(this.mockInstance, method(TrailDBj.class, "tdbConsInit")).withNoArguments().thenReturn(null);

        // Call constructor which calls the init C method.
        new TrailDBConstructor("asdf", new String[] { "f1", "f2" });
    }

    @Test
    public void addFailure() throws Exception {

        // Test that the correct exception is thrown when adding an event fails (C lib failure).

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to add: -1");

        // Because the whole class is a mock, we have to mock all methods and make them succeed in order to further call
        // the add method.
        PowerMockito.when(this.mockInstance, method(TrailDBj.class, "tdbConsInit")).withNoArguments()
                .thenReturn(this.someBB);

        PowerMockito.when(this.mockInstance, method(TrailDBj.class, "UUIDRaw", String.class))
                .withArguments(anyString())
                .thenReturn(this.someBytes);
        PowerMockito
                .when(this.mockInstance,
                        method(TrailDBj.class, "tdbConsAdd", ByteBuffer.class, byte[].class, long.class, String[].class,
                                long[].class))
                .withArguments(anyObject(), anyObject(), anyLong(), anyObject(), anyObject())
                .thenReturn(-1);

        // Call the code that will throw.
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.add(this.cookie, 120, new String[] { "a", "hinata" });
    }

}
