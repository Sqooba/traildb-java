package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.sqooba.traildbj.TrailDBj.TrailDB;
import io.sqooba.traildbj.TrailDBj.TrailDBConstructor;
import io.sqooba.traildbj.TrailDBj.TrailDBError;

public class TrailDBTest {

    private TrailDB db;
    private String path = "testdb";

    @Before
    public void setUp() throws IOException {
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "field1", "field2" });
        String cookie = "12345678123456781234567812345678";
        String cookie2 = "345830947509385384759384795384593874";
        cons.add(cookie, 120, new String[] { "a", "hinata" });
        cons.add(cookie, 121, new String[] { "vilya", "" });
        cons.add(cookie2, 122, new String[] { "kaguya", "hinata" });
        cons.add(cookie2, 123, new String[] { "alongstring", "averyveryverylongstring" });
        this.db = cons.finalise();
        cons.close();
    }

    @Test
    public void constructionShouldCreateFile() throws IOException {
        File f = new File(this.path + ".tdb");
        assertTrue(f.exists() && !f.isDirectory());
    }

    @SuppressWarnings("resource")
    @Test(expected = NullPointerException.class)
    public void constructionShouldFailWithNullPath() {
        new TrailDBConstructor(null, new String[] { "" });
    }

    @Test(expected = TrailDBError.class)
    public void addShouldFailIfValNbrNotEqualFieldNbr() throws IOException {
        TrailDBConstructor cons = new TrailDBConstructor(this.path, new String[] { "" });
        cons.add("c", 1, new String[] { "a", "b" });
        cons.close();
    }

    @Test
    public void metaDataShouldBeCorrect() {
        assertEquals(2, this.db.length());
        assertEquals(120, this.db.getMinTimestamp());
        assertEquals(123, this.db.getMaxTimestamp());
        assertEquals(1, this.db.getVersion());
    }

    @Test
    public void itemsFieldsAndValuesShouldBeCorrect() {
        // Lexicon.
        assertEquals(5, this.db.getLexiconSize(1));
        assertEquals(3, this.db.getLexiconSize(2));
        assertEquals(1, this.db.getField("field1"));
        assertEquals(2, this.db.getField("field2"));

        // FieldName.
        assertEquals("field1", this.db.getFieldName(1));
        assertEquals("field2", this.db.getFieldName(2));

        // ItemValue + Item.
        assertEquals("a", this.db.getItemValue(this.db.getItem(1, "a")));
        assertEquals("vilya", this.db.getItemValue(this.db.getItem(1, "vilya")));
        assertEquals("kaguya", this.db.getItemValue(this.db.getItem(1, "kaguya")));
        assertEquals("alongstring", this.db.getItemValue(this.db.getItem(1, "alongstring")));
        assertEquals("hinata", this.db.getItemValue(this.db.getItem(2, "hinata")));
        assertEquals("", this.db.getItemValue(this.db.getItem(2, "")));
        assertEquals("hinata", this.db.getItemValue(this.db.getItem(2, "hinata")));
        assertEquals("averyveryverylongstring", this.db.getItemValue(this.db.getItem(2, "averyveryverylongstring")));

        // Value. Empty string NOT present.
        assertEquals("a", this.db.getValue(1, 1));
        assertEquals("vilya", this.db.getValue(1, 2));
        assertEquals("kaguya", this.db.getValue(1, 3));
        assertEquals("alongstring", this.db.getValue(1, 4));
        assertEquals("hinata", this.db.getValue(2, 1));
        assertEquals("averyveryverylongstring", this.db.getValue(2, 2));
    }

    @Test
    public void appendCorrectly() throws IOException {
        TrailDBConstructor cons2 = new TrailDBConstructor(this.path + "2", new String[] { "field1", "field2" });
        cons2.add("12345", 119, new String[] { "asdf", "qwer" });
        cons2.append(this.db);
        TrailDB db2 = cons2.finalise();
        cons2.close();

        File f = new File(this.path + "2" + ".tdb");
        assertTrue(f.exists() && !f.isDirectory());

        assertEquals("kaguya", db2.getValue(1, 4));
        assertTrue(this.db.length() + 1 == db2.length());
        assertTrue(this.db.getMaxTimestamp() == db2.getMaxTimestamp());
        assertTrue(this.db.getMinTimestamp() != db2.getMinTimestamp());

        // Cleanup.
        f.delete();
        FileUtils.deleteDirectory(new File(this.path + "2"));
    }

    @Test(expected = TrailDBError.class)
    public void appendShouldFailInCaseDifferentFields() throws IOException {
        TrailDBConstructor cons3 = new TrailDBConstructor(this.path + "3", new String[] { "f1", "f2" });
        try {
            cons3.append(this.db);
        } catch(TrailDBError e) {
            throw e;
        } finally {
            cons3.close();
            FileUtils.deleteDirectory(new File(this.path + "3"));
        }
    }

    @After
    public void tearDown() throws IOException {
        File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
    }
}
