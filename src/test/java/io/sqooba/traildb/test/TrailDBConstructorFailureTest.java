package io.sqooba.traildb.test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDBConstructor;
import io.sqooba.traildb.TrailDBError;
import io.sqooba.traildb.TrailDBNative;
import mockit.Expectations;

public class TrailDBConstructorFailureTest {

    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

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
    public void consInitFailure() {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to allocate memory for constructor.");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.consInit();
                this.result = null;
            }
        };
        new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
    }

    @Test
    public void consOpenFailure() throws Exception {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Can not open constructor.");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.consOpen((ByteBuffer)this.any, this.anyString, (String[])this.any, this.anyLong);
                this.result = -1;
            }
        };
        new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
    }

    @Test
    public void consAddFailure() {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to add: -1");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.consAdd((ByteBuffer)this.any, (byte[])this.any, this.anyLong, (String[])this.any,
                        (long[])this.any);
                this.result = -1;
            }
        };
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.add(this.cookie, 120, new String[] { "a", "hinata" });
    }

    @Test
    public void consAppendFailure() {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to merge dbs: -1");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.consAppend((ByteBuffer)this.any, (ByteBuffer)this.any);
                this.result = -1;
            }
        };
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        TrailDB db = cons.finalise();
        cons.append(db);
    }

    @Test
    public void consFinaliseFailure() {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Failed to finalize.");

        final TrailDBNative traildbj = TrailDBNative.INSTANCE;
        new Expectations(traildbj) {

            {
                traildbj.consFinalize((ByteBuffer)this.any);
                this.result = -1;
            }
        };
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.finalise();
    }

}
