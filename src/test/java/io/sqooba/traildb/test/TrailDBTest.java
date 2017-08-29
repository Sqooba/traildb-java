package io.sqooba.traildb.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.sqooba.traildb.TrailDB;
import io.sqooba.traildb.TrailDBEvent;
import io.sqooba.traildb.TrailDBException;
import io.sqooba.traildb.TrailDBIterator;
import io.sqooba.traildb.TrailDBNative;
import mockit.Deencapsulation;

public class TrailDBTest {

    private TrailDB db;
    private final String path = "testdb";
    private final String cookie = "12345678123456781234567812345678";
    private final String otherCookie = "12121212121212121212121212121212";

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

    @Test
    public void trailsShouldContainCorrectTrailUUIDs() {
        final Map<String, TrailDBIterator> map = this.db.trails();
        assertEquals(2, map.size());

        final Iterator<Map.Entry<String, TrailDBIterator>> it = map.entrySet().iterator();
        assertEquals(this.otherCookie, it.next().getKey());
        assertEquals(this.cookie, it.next().getKey());
    }

    @Test
    public void trailShouldContainCorrectNumberOfTrailDBEvents() {
        TrailDBIterator trail = this.db.trail(0);
        int count = 0;
        for(final TrailDBEvent TrailDBEvent : trail) {
            count++;
        }
        assertEquals(2, count);

        trail = this.db.trail(1);
        count = 0;
        for(final TrailDBEvent TrailDBEvent : trail) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void trailShouldContainCorrectTrailDBEvents() {
        final TrailDBIterator trail = this.db.trail(0);
        final TrailDBEvent e = trail.iterator().next();
        final String[] fieldsNames = e.getFieldNames();

        assertEquals(122, e.getTimestamp());
        assertEquals(2, e.getNumItems());
        assertEquals("time", fieldsNames[0]);
        assertEquals("field1", fieldsNames[1]);
        assertEquals("field2", fieldsNames[2]);
        assertEquals("Event(time=122, field1=kaguya, field2=hinata)", e.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void trailRemoveShouldThrow() {
        final TrailDBIterator trail = this.db.trail(0);
        trail.iterator().remove();
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

    @Test(expected = IllegalArgumentException.class)
    public void openTrailDBOnNullPathShouldFail() {
        try (TrailDB db = new TrailDB(null)) {
            // To auto close.
        }
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

    @Test(expected = TrailDBException.class)
    public void getTrailIDShouldFailWithWrongUUID() {
        this.db.getTrailID("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    @Test(expected = TrailDBException.class)
    public void getFieldShouldFailOnNonExistentField() {
        this.db.getField("wrongfield");
    }

    @Test(expected = TrailDBException.class)
    public void getLexiconSizeShouldFailOnWrongFieldIndex() {
        this.db.getLexiconSize(-1);
    }

    @Test(expected = TrailDBException.class)
    public void getFieldNameShouldFailOnInvalidFieldId() {
        this.db.getFieldName(-1);
    }

    @Test(expected = TrailDBException.class)
    public void getItemShouldFailIfNoItemFound() {
        this.db.getItem(-1, "wrong");
    }

    @Test(expected = TrailDBException.class)
    public void getValueShouldFailIfValueNotFound() {
        this.db.getValue(-1, -1);
    }

    @Test(expected = TrailDBException.class)
    public void getItemValueShouldFailIfValueNotFound() {
        this.db.getItemValue(-1);
    }

    @Test
    public void getFieldShouldReturnCorrectID() {
        final long handle = Deencapsulation.getField(this.db, "db");
        final ByteBuffer res = ByteBuffer.allocate(4);
        TrailDBNative.INSTANCE.getField(handle, "field1", res);
        assertEquals(1, res.getInt(0));
    }

    @Test
    public void closeShouldFreeDB() {
        this.db.close();
        assertEquals(-1, (long)Deencapsulation.getField(this.db, "db"));
    }

    @Test
    public void closeInvalidHandle() {

        Deencapsulation.setField(this.db, "db", -1);
        this.db.close();
    }

    @After
    public void tearDown() throws IOException {

        // Clear the TrailDB files/directories created for the tests.
        final File f = new File(this.path + ".tdb");
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        FileUtils.deleteDirectory(new File(this.path));
        this.db.close();
    }
}
