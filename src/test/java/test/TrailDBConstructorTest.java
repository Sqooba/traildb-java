package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildbjava.TrailDB;
import io.sqooba.traildbjava.TrailDBConstructor;
import io.sqooba.traildbjava.TrailDBError;

public class TrailDBConstructorTest {

    private TrailDB db;
    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";
    private String otherCookie = "12121212121212121212121212121212";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws IOException {

        // Initialise a TrailDB with some TrailDBEvents.
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.add(this.cookie, 120, new String[] { "a", "hinata" });
        cons.add(this.cookie, 121, new String[] { "vilya", "" });
        cons.add(this.otherCookie, 122, new String[] { "kaguya", "hinata" });
        cons.add(this.otherCookie, 123, new String[] { "alongstring", "averyveryverylongstring" });
        this.db = cons.finalise();
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
    public void constructionShouldCreateFile() throws IOException {
        File f = new File(this.path + ".tdb");
        assertTrue(f.exists() && !f.isDirectory());
    }

    @Test(expected = NullPointerException.class)
    public void constructionShouldFailWithNullPath() {
        new TrailDBConstructor(null, new String[] { "" });
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionShouldFailWithEmptyFieldName() throws IOException {
        new TrailDBConstructor(this.path, new String[] { "", "" });

    }

    @Test
    public void addingToAlreadyFinalisedDBShouldFail() {
        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Trying to add event to an already finalised database.");

        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        cons.finalise();
        cons.add(this.cookie, 120, new String[] { "a", "hinata" });
    }

    @Test
    public void addShouldFailIfValNbrNotEqualFieldNbr() throws IOException {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Number of values does not match number of fields.");

        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "f1" });
        cons.add("c", 1, new String[] { "a", "b" });
    }

    @Test(expected = IllegalArgumentException.class)
    public void addShouldFailWithInvalidUUID() throws IOException {
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "f1", "f2" });
        cons.add("c", 1, new String[] { "a", "b" });
    }

    @Test
    public void appendCorrectly() throws IOException {
        TrailDBConstructor otherCons = new TrailDBConstructor(this.path + "other", new String[] { "field1", "field2" });
        otherCons.add("11111111111111111111111111111111", 119, new String[] { "asdf", "qwer" });
        otherCons.append(this.db);
        TrailDB db2 = otherCons.finalise();

        File f = new File(this.path + "other" + ".tdb");
        assertTrue(f.exists() && !f.isDirectory());

        assertEquals("kaguya", db2.getValue(1, 4));
        assertTrue(this.db.length() + 1 == db2.length());
        assertTrue(this.db.getMaxTimestamp() == db2.getMaxTimestamp());
        assertTrue(this.db.getMinTimestamp() != db2.getMinTimestamp());

        // Cleanup.
        f.delete();
        FileUtils.deleteDirectory(new File(this.path + "other"));
    }

    @Test(expected = TrailDBError.class)
    public void appendShouldFailInCaseDifferentFields() throws IOException {
        TrailDBConstructor failCons = new TrailDBConstructor(this.path + "fail", new String[] { "f1", "f2" });
        try {
            failCons.append(this.db);
        } catch(TrailDBError e) {
            throw e;
        } finally {
            FileUtils.deleteDirectory(new File(this.path + "fail"));
        }
    }

}
