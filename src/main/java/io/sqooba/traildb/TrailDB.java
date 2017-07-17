package io.sqooba.traildb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to query an existing TrailDB.
 * 
 * @author B. Sottas
 *
 */
public class TrailDB implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrailDB.class);

    /** 8 bytes are need to represents 64 bits. */
    private static final int UINT64 = 8;

    private TrailDBNative trailDBj = TrailDBNative.INSTANCE; // FixMe: prefer no singletons

    /** ByteBuffer holding a pointer to the traildb. */
    ByteBuffer db;

    private long numTrails;
    private long numEvents;
    private long numFields;
    private List<String> fields;

    private TrailDB(TrailDBBuilder builder) {
        this(builder.path);
    }

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
            LOGGER.warn("long overflow, received a negtive value for min timestamp.");
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
            LOGGER.warn("long overflow, received a negtive value for max timestamp.");
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
            LOGGER.warn("version overflow.");
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
            LOGGER.warn("Returned item overflow, deal with it carefully!");
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
            LOGGER.warn("Received trail ID overflow: " + res);
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
    public TrailDBIterator trail(long trailID) { // Python has more params.
        ByteBuffer cursor = this.trailDBj.cursorNew(this.db);
        if (cursor == null) {
            throw new TrailDBError("Memory allocation failed for cursor.");
        }
        int errCode = this.trailDBj.getTrail(cursor, trailID);
        if (errCode != 0) {
            throw new TrailDBError("Failed to create cursor with code: " + errCode);
        }
        TrailDBEvent e = new TrailDBEvent(this, this.fields);
        return new TrailDBIterator(cursor, e);
    }

    /**
     * Get a map containing a trail UUID as key and a trail cursor as value, allowing to iterate over all trails in the
     * database.
     * 
     * @return A map containing a trail UUID as key and a trail cursor as value
     */
    public Map<String, TrailDBIterator> trails() {
        Map<String, TrailDBIterator> res = new HashMap<>();
        for(int i = 0; i < this.length(); i++) {
            res.put(this.getUUID(i), this.trail(i));
        }

        return res;
    }

    @Override
    public void close() {
        if (this.db != null) {
            this.trailDBj.close(this.db);
            this.db = null;
        }
    }

    /**
     * Class allowing to easily construct a new TrailDB.
     * 
     * @author B. Sottas
     */
    public static class TrailDBBuilder {

        private static final Logger LOGGER = LoggerFactory.getLogger(TrailDBBuilder.class);

        private TrailDBNative trailDBj = TrailDBNative.INSTANCE;

        /** New TrailDB output path, without .tdb. */
        private String path;

        /** Names of fields in the new TrailDB. */
        private String[] ofields;

        /** Handle to the TrailDB, returned by init method. */
        private ByteBuffer cons;

        /** Tells if this Builder build method has already been called. */
        private boolean closed = false;

        /**
         * Build a new TrailDB.
         * 
         * @param path TrailDB output path.
         * @param ofields Names of fields.
         * @throws NullPointerException If given path is null.
         * @throws TrailDBError If allocation fails or can not open constructor.
         */
        public TrailDBBuilder(String path, String[] ofields) {
            if (path == null) {
                throw new NullPointerException("Path must not be null.");
            }
            if (Arrays.asList(ofields).contains("")) {
                throw new IllegalArgumentException("Fields must not contain empty String.");
            }

            // Initialisation.
            this.cons = this.trailDBj.consInit();
            if (this.cons == null) {
                throw new TrailDBError("Failed to allocate memory for constructor.");
            }
            if (this.trailDBj.consOpen(this.cons, path, ofields, ofields.length) != 0) {
                throw new TrailDBError("Can not open constructor.");
            }

            this.path = path;
            this.ofields = ofields;
        }

        /*
         * FixMe public void add(Event e) { this.add(e.uuid, e.timestamp, e.values); };
         */

        /**
         * Add an event to the TrailDB.
         * 
         * @param uuid UUID of the event to be added.
         * @param timestamp Event timestamp.
         * @param values Value of each field.
         * @throws TrailDBError if number of values does not match number of fields or failed to add to the DB.
         * @throws IllegalArgumentException If {@code uuid} is an invalid 32-byte hex string.
         */
        public TrailDBBuilder add(String uuid, long timestamp, String[] values) {
            if (this.closed) {
                throw new TrailDBError("Trying to add event to an already finalised database.");
            }

            int n = values.length;
            if (n != this.ofields.length) {
                // FIXME this is a hack to avoid random errors in the C lib.
                // Need to investigate add function in JNI.
                throw new TrailDBError("Number of values does not match number of fields.");
            }
            long[] value_lengths = new long[n];
            for(int i = 0; i < n; i++) {
                value_lengths[i] = values[i].length();
            }

            byte[] rawUUID = this.trailDBj.UUIDRaw(uuid);
            if (rawUUID == null) {
                throw new IllegalArgumentException("uuid is invalid.");
            }
            int errCode = this.trailDBj.consAdd(this.cons, rawUUID, timestamp, values, value_lengths);
            if (errCode != 0) {
                throw new TrailDBError("Failed to add: " + errCode);
            }

            return this;
        }

        /**
         * Merge an existing TrailDB to this constructor. The fields must be equal between the existing and the new
         * TrailDB.
         * 
         * @param db The db to merge to this one.
         * @throws TrailDBError if the merge fails.
         */
        public TrailDBBuilder append(TrailDB db) {
            if (this.closed) {
                throw new TrailDBError("Trying to append to an already finalised database.");
            }

            int errCode = this.trailDBj.consAppend(this.cons, db.db);
            if (errCode != 0) {
                throw new TrailDBError("Failed to merge dbs: " + errCode);
            }

            return this;
        }

        public TrailDB build() {
            this.closed = true; // Prevent add/append calls after building.
            if (this.trailDBj.consFinalize(this.cons) != 0) {
                throw new TrailDBError("Failed to finalize.");
            }
            LOGGER.info("Finalisation done.");

            this.trailDBj.consClose(this.cons);
            LOGGER.info("TrailDBBuilder closed.");
            this.cons = null;

            return new TrailDB(this);
        }
    }

}
