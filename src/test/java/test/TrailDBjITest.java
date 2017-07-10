package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildbj.TrailDB;
import io.sqooba.traildbj.TrailDBConstructor;
import io.sqooba.traildbj.TrailDBCursor;
import io.sqooba.traildbj.TrailDBError;
import io.sqooba.traildbj.TrailDBEvent;

public class TrailDBjITest {

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

    @Test
    public void trailsShouldContainCorrectTrailUUIDs() {
        Map<String, TrailDBCursor> map = this.db.trails();
        assertEquals(2, map.size());

        Iterator<Map.Entry<String, TrailDBCursor>> it = map.entrySet().iterator();
        assertEquals(this.otherCookie, it.next().getKey());
        assertEquals(this.cookie, it.next().getKey());
    }

    @Test
    public void trailShouldContainCorrectNumberOfTrailDBEvents() {
        TrailDBCursor trail = this.db.trail(0);
        int count = 0;
        for(TrailDBEvent TrailDBEvent : trail) {
            count++;
        }
        assertEquals(2, count);

        trail = this.db.trail(1);
        count = 0;
        for(TrailDBEvent TrailDBEvent : trail) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void trailShouldContainCorrectTrailDBEvents() {
        TrailDBCursor trail = this.db.trail(0);
        TrailDBEvent e = trail.iterator().next();
        List<String> fieldsNames = e.getFieldNames();
        List<String> fieldsValues = e.getFieldsValues();

        assertEquals(122, e.getTimestamp());
        assertEquals(2, e.getNumItems());
        assertEquals("time", fieldsNames.get(0));
        assertEquals("field1", fieldsNames.get(1));
        assertEquals("field2", fieldsNames.get(2));
        assertEquals("kaguya", fieldsValues.get(0));
        assertEquals("hinata", fieldsValues.get(1));
        assertEquals("Event(time=122, field1=kaguya, field2=hinata)", e.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void trailRemoveShouldThrow() {
        TrailDBCursor trail = this.db.trail(0);
        trail.iterator().remove();
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

    @Test(expected = IllegalArgumentException.class)
    public void openTrailDBOnNullPathShouldFail() {
        new TrailDB(null);
    }

    @Test
    public void getUUIDAndGetTrailIDReturnCorrectValues() {
        assertEquals(this.cookie, this.db.getUUID(this.db.getTrailID(this.cookie)));
        assertEquals(this.otherCookie, this.db.getUUID(this.db.getTrailID(this.otherCookie)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUUIDShouldFailWithWrongUUID() {
        this.db.getUUID(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUUIDShouldFailWithWrongUUIDBis() {
        this.db.getUUID(1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTrailIDShouldFailWithInvalidString() {
        this.db.getTrailID("invalidhex");
    }

    @Test(expected = TrailDBError.class)
    public void getTrailIDShouldFailWithWrongUUID() {
        this.db.getTrailID("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test(expected = TrailDBError.class)
    public void getFieldShouldFailOnNonExistentField() {
        this.db.getField("wrongfield");
    }

    @Test(expected = TrailDBError.class)
    public void getLexiconSizeShouldFailOnWrongFieldIndex() {
        this.db.getLexiconSize(-1);
    }

    @Test(expected = TrailDBError.class)
    public void getFieldNameShouldFailOnInvalidFieldId() {
        this.db.getFieldName(-1);
    }

    @Test(expected = TrailDBError.class)
    public void getItemShouldFailIfNoItemFound() {
        this.db.getItem(-1, "wrong");
    }

    @Test(expected = TrailDBError.class)
    public void getValueShouldFailIfValueNotFound() {
        this.db.getValue(-1, -1);
    }

    @Test(expected = TrailDBError.class)
    public void getItemValueShouldFailIfValueNotFound() {
        this.db.getItemValue(-1);
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
}
