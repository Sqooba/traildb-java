package io.sqooba.traildb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class used to query an existing TrailDB.
 * 
 * @author B. Sottas
 *
 */
public class TrailDB {

    private static final Logger LOGGER = Logger.getLogger(TrailDB.class.getName());

    /** 8 bytes are need to represents 64 bits. */
    private static final int UINT64 = 8;

    private TrailDBNative trailDBj = TrailDBNative.INSTANCE; // FixMe: prefer no singletons

    /** ByteBuffer holding a pointer to the traildb. */
    ByteBuffer db;

    private long numTrails;
    private long numEvents;
    private long numFields;
    private List<String> fields;

    /**
     * Construct a TrailDB on the given .tdb file.
     * 
     * @param path The path to the TrailDB file.
     */
    public TrailDB(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null.");
        }

        ByteBuffer db = this.trailDBj.init();
        this.db = db;

        if (this.trailDBj.open(this.db, path) != 0) {
            throw new TrailDBError("Failed to open db.");
        }

        this.numTrails = this.trailDBj.numTrails(db);
        this.numEvents = this.trailDBj.numEvents(db);
        this.numFields = this.trailDBj.numFields(db);
        this.fields = new ArrayList<>((int)this.numFields);

        for(int i = 0; i < this.numFields; i++) {
            this.fields.add(this.trailDBj.getFieldName(this.db, i));
        }
    }

    /**
     * Return the number of trails in the TrailDB.
     * 
     * @return The number of trails.
     */
    public long length() {
        return this.numTrails;
    }

    /**
     * Get the oldest timestamp.
     * 
     * @return The oldest timestamp.
     */
    public long getMinTimestamp() {
        long min = this.trailDBj.minTimestamp(this.db);
        if (min < 0) {
            LOGGER.log(Level.WARNING, "long overflow, received a negtive value for min timestamp.");
        }
        return min;
    }

    /**
     * Get the newest timestamp.
     * 
     * @return The newest timestmap.
     */
    public long getMaxTimestamp() {
        long max = this.trailDBj.maxTimestamp(this.db);
        if (max < 0) {
            LOGGER.log(Level.WARNING, "long overflow, received a negtive value for max timestamp.");
        }
        return max;
    }

    /**
     * Get the version.
     * 
     * @return The version.
     */
    public long getVersion() {
        long version = this.trailDBj.version(this.db);
        if (version < 0) {
            LOGGER.log(Level.WARNING, "version overflow.");
        }
        return version;
    }

    /**
     * Get the field ID given a field name.
     * 
     * @param fieldName The field name.
     * @return The corresponding field ID.
     * @throws TrailDBError if the specified field is not found.
     */
    public long getField(String fieldName) {
        long index = this.fields.indexOf(fieldName);
        if (index == -1) {
            throw new TrailDBError("Failed to retreive field. Field not found");
        }
        return index;
    }

    /**
     * Get the number of distinct values in the given field, +1 counting the empty string if not present.
     * 
     * @param field The field ID.
     * @return The number of distinct values.
     * @throws TrailDBError if the field index is invalid ( <=0 | > number of fields).
     */
    public long getLexiconSize(long field) {
        long value = this.trailDBj.lexiconSize(this.db, field);
        if (value == 0) {
            throw new TrailDBError("Invalid field index.");
        }
        return value;
    }

    /**
     * Get the field name given a field ID.
     * 
     * @param fieldId The field ID.
     * @return The corresponding field name.
     * @throws TrailDBError if the field id is invalid ( <=0 | > number of fields).
     */
    public String getFieldName(long fieldId) {
        String res = this.trailDBj.getFieldName(this.db, fieldId);
        if (res == null) {
            throw new TrailDBError("Invalid field id.");
        }
        return res;
    }

    /**
     * <p>Get the item corresponding to a value. Note that this is a relatively slow operation that may need to scan
     * through all values in the field.
     * 
     * <p> WARNING: the returned value maybe suffer overflow!
     * 
     * @param fieldID The field ID.
     * @param value The value in the field.
     * @return An item encoded in a long, which was casted from uint64_t.
     * @throws TrailDBError if no item is found.
     */
    public long getItem(long fieldID, String value) {
        long item = this.trailDBj.getItem(this.db, fieldID, value);
        if (item == 0) {
            throw new TrailDBError("No item found.");
        }
        if (item < 0) {
            LOGGER.warning("Returned item overflow, deal with it carefully!");
        }
        return item;
    }

    /**
     * <p> Get the value corresponding to a field ID and value ID pair.
     * 
     * <p> Calling with the empty string for {@code val} will result in an error.
     * 
     * @param field The field ID.
     * @param val The value ID.
     * @return The corresponding field value.
     * @throws TrailDBError if the returned value is too big for Java or not found in the db.
     */
    public String getValue(long field, long val) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        String value = this.trailDBj.getValue(this.db, field, val, bb);
        if (value == null) {
            throw new TrailDBError("Error reading value.");
        }
        long value_length = bb.getLong(0);
        if (value_length > Integer.MAX_VALUE) {
            throw new TrailDBError(
                    "Overflow, received a String value that is larger than the java String capacity.");
        }
        return value.substring(0, (int)value_length);
    }

    /**
     * Get the value corresponding to an item. This is a shorthand version of getValue().
     * 
     * @param item The item.
     * @return The corresponding field value.
     * @throws TrailDBError if the value was not found or the returned value is too big for Java.
     */
    public String getItemValue(long item) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        String value = this.trailDBj.getItemValue(this.db, item, bb);
        if (value == null) {
            throw new TrailDBError("Value not found.");
        }
        long value_length = bb.getLong(0);
        if (value_length > Integer.MAX_VALUE) {
            throw new TrailDBError(
                    "Overflow, received a String value that is larger than the java String capacity.");
        }
        return value.substring(0, (int)value_length);
    }

    /**
     * Get the UUID given a trail ID.
     * 
     * @param trailID The trail ID.
     * @return A raw 16-byte UUID.
     * @throws TrailDBError if the {@code trailID} is invalid i.e. less than 0 or greater than the number of trails.
     * @throws IllegalArgumentException If {@code trailID} is less than 0 or >= than the number of trails.
     */
    public String getUUID(long trailID) {
        if (trailID < 0 || trailID >= this.length()) {
            throw new IllegalArgumentException("Invalid trail ID.");
        }

        ByteBuffer uuid = this.trailDBj.getUUID(this.db, trailID);
        if (uuid == null) {
            throw new TrailDBError("Invalid trail ID.");
        }

        byte[] bytes = new byte[uuid.capacity()];
        uuid.position(0);
        uuid.get(bytes, 0, uuid.capacity());
        return this.trailDBj.UUIDHex(bytes);
    }

    /**
     * Get the trail ID given a UUID.
     *
     * @param uuid A raw 16-byte UUID.
     * @return The traild ID corresponding to {@code uuid}.
     * @throws TrailDBError if the UUID was not found.
     * @throws IllegalArgumentException If {@code uuid} is an invalid 32-byte hex string.
     */
    public long getTrailID(String uuid) {
        ByteBuffer trailID = ByteBuffer.allocate(UINT64);
        byte[] rawUUID = this.trailDBj.UUIDRaw(uuid);
        if (rawUUID == null) {
            throw new IllegalArgumentException("Invalid UUID.");
        }
        int errCode = this.trailDBj.getTrailId(this.db, rawUUID, trailID);
        if (errCode != 0) {
            throw new TrailDBError("UUID not found. " + errCode);
        }
        long res = trailID.getLong(0);
        if (res < 0) {
            LOGGER.warning("Received trail ID overflow: " + res);
        }
        return res;
    }

    /**
     * Get a cursor over a particular trail in the database. The cursor allows to iterate over events in this trail.
     * 
     * @param trailID The trail id.
     * @return A cursor over the trail.
     * @throws TrailDBError If cursor creation failed in some way.
     */
    public TrailDBCursor trail(long trailID) { // Python has more params.
        ByteBuffer cursor = this.trailDBj.cursorNew(this.db);
        if (cursor == null) {
            throw new TrailDBError("Memory allocation failed for cursor.");
        }
        int errCode = this.trailDBj.getTrail(cursor, trailID);
        if (errCode != 0) {
            throw new TrailDBError("Failed to create cursor with code: " + errCode);
        }
        TrailDBEvent e = new TrailDBEvent(this, this.fields);
        return new TrailDBCursor(cursor, e);
    }

    /**
     * Get a map containing a trail UUID as key and a trail cursor as value, allowing to iterate over all trails in the
     * database.
     * 
     * @return A map containing a trail UUID as key and a trail cursor as value
     */
    public Map<String, TrailDBCursor> trails() {
        Map<String, TrailDBCursor> res = new HashMap<>();
        for(int i = 0; i < this.length(); i++) {
            res.put(this.getUUID(i), this.trail(i));
        }

        return res;
    }

    // FixMe: implement AutoClosable and use close();
    @Override
    protected void finalize() {
        if (this.db != null) {
            this.trailDBj.close(this.db);
            this.db = null;
        }
    }
}
