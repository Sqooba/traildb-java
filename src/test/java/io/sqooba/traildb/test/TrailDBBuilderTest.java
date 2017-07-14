package io.sqooba.traildb.test;

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

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDB.TrailDBBuilder;
import io.sqooba.traildb.TrailDBError;

public class TrailDBBuilderTest {

    private TrailDB db;
    private String path = "testdb";
    private String cookie = "12345678123456781234567812345678";
    private String otherCookie = "12121212121212121212121212121212";

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws IOException {

        // Initialise a TrailDB with some TrailDBEvents.
        this.db = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" })
                .add(this.cookie, 120, new String[] { "a", "hinata" })
                .add(this.cookie, 121, new String[] { "vilya", "" })
                .add(this.otherCookie, 122, new String[] { "kaguya", "hinata" })
                .add(this.otherCookie, 123, new String[] { "alongstring", "averyveryverylongstring" })
                .build();
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

    @Test
    public void buildingUnchained() throws IOException {
        TrailDBBuilder builder = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" });
        builder.add(this.cookie, 120, new String[] { "a", "hinata" });
        builder.add(this.cookie, 121, new String[] { "vilya", "" });
        builder.add(this.otherCookie, 122, new String[] { "kaguya", "hinata" });
        builder.add(this.otherCookie, 123, new String[] { "alongstring", "averyveryverylongstring" });
        builder.build();

        constructionShouldCreateFile();
    }

    @Test(expected = NullPointerException.class)
    public void constructionShouldFailWithNullPath() {
        new TrailDB.TrailDBBuilder(null, new String[] { "" });
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructionShouldFailWithEmptyFieldName() throws IOException {
        new TrailDB.TrailDBBuilder(this.path, new String[] { "", "" });

    }

    @Test
    public void addShouldFailIfValNbrNotEqualFieldNbr() throws IOException {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Number of values does not match number of fields.");

        new TrailDB.TrailDBBuilder(this.path, new String[] { "f1" }).add("c", 1, new String[] { "a", "b" });

    }

    @Test(expected = IllegalArgumentException.class)
    public void addShouldFailWithInvalidUUID() throws IOException {
        new TrailDB.TrailDBBuilder(this.path, new String[] { "f1", "f2" }).add("c", 1, new String[] { "a", "b" });
    }

    @Test
    public void appendCorrectly() throws IOException {
        TrailDB db2 = new TrailDB.TrailDBBuilder(this.path + "other", new String[] { "field1", "field2" })
                .add("11111111111111111111111111111111", 119, new String[] { "asdf", "qwer" })
                .append(this.db)
                .build();

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
        try {
            new TrailDB.TrailDBBuilder(this.path + "fail", new String[] { "f1", "f2" })
                    .append(this.db);
        } catch(TrailDBError e) {
            throw e;
        } finally {
            FileUtils.deleteDirectory(new File(this.path + "fail"));
        }
    }

    @Test
    public void addingToAlreadyFinalisedDBShouldFail() {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Trying to add event to an already finalised database.");

        TrailDBBuilder builder = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" });
        builder.build();
        builder.add(this.cookie, 121, new String[] { "vilya", "" });
    }

    @Test
    public void appendingToAlreadyFinalisedSBShouldFail() {

        this.expectedEx.expect(TrailDBError.class);
        this.expectedEx.expectMessage("Trying to append to an already finalised database.");

        TrailDBBuilder builder = new TrailDB.TrailDBBuilder(this.path, new String[] { "field1", "field2" });
        builder.add(this.cookie, 121, new String[] { "vilya", "" });
        builder.build();
        builder.append(new TrailDB(this.path));
    }

}
