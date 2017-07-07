package io.sqooba.traildbj;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * This class is used to perform native call to the TrailDB C library. Base on the available Python bindings.
 * 
 * @author Vilya
 *
 */
public enum TrailDBj {

    INSTANCE;

    /**
     * Convert a raw 16-byte UUID into its hexadecimal string representation.
     * 
     * @param rawUUID 16-byte UUID.
     * @return A 32-byte hexadecimal string representation.
     */
    public String UUIDHex(byte[] rawUUID) {
        return Hex.encodeHexString(rawUUID);
    }

    /**
     * Convert a 32-byte hexadecimal string representation of an UUID into a raw 16-byte UUID.
     * 
     * @param hexUUID The UUID to be converted.
     * @return The raw 16-byte UUID.
     */
    public byte[] UUIDRaw(String hexUUID) {
        byte[] b = null;
        try {
            b = Hex.decodeHex(hexUUID.toCharArray());
        } catch(DecoderException e) {
            LOGGER.log(Level.SEVERE, "Failed to convert hexstring to string.", e);
        }
        return b;
    }

    private static final Logger LOGGER = Logger.getLogger(TrailDBj.class.getName());

    /** 8 bytes are need to represents 64 bits. */
    private static final int UINT64 = 8;

    static {
        loadLib("traildb4j");
    }

    /**
     * Extract the library from the jar/project, copy it outside so we can load it because this is not possible from
     * inside the jar.
     * 
     * @param name The name of the library, without prefix/suffix.
     */
    private static void loadLib(String name) {
        name = System.mapLibraryName(name);
        try {
            InputStream in = TrailDBj.class.getResourceAsStream("/" + name);

            File dirOut = new File("TrailDBWrapper/");
            dirOut.mkdir();

            File fileOut = File.createTempFile("traildb4j", name.substring(name.indexOf(".")), dirOut);

            System.out.println("Writing lib to: " + fileOut.getAbsolutePath());
            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            in.close();
            out.close();

            fileOut.deleteOnExit();

            System.load(fileOut.getAbsolutePath());
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load library.", e);
            System.exit(-1);
        }
    }

    // ========================================================================
    // Construct a new TrailDB
    // ========================================================================

    /** tdb_cons *tdb_cons_init(void) */
    private native ByteBuffer tdbConsInit();

    /** tdb_error tdb_cons_open(tdb_const *cons, const char *root, const char **ofield_names, uint64_t num_ofields) */
    private native int tdbConsOpen(ByteBuffer cons, String root, String[] ofieldNames, long numOfields);

    /** void tdb_cons_close(tdb_cons *cons) */
    private native void tdbConsClose(ByteBuffer cons);

    /**
     * tdb_error tdb_cons_add(tdb_cons *cons,const uint8_t uuid[16],const uint64_t timestamp,const char **values,const
     * uint64_t *value_lengths)
     */
    private native int tdbConsAdd(ByteBuffer cons, byte[] uuid, long timestamp, String[] values,
            long[] valueLengths);

    /** tdb_error tdb_cons_append(tdb_cons *cons, const tdb *db) */
    private native int tdbConsAppend(ByteBuffer cons, ByteBuffer db);

    /** tdb_error tdb_cons_finalize(tdb_cons *cons) */
    private native int tdbConsFinalize(ByteBuffer cons);

    // ========================================================================
    // Open a TrailDB and access metadata.
    // ========================================================================

    /** tdb *tdb_init(void) */
    private native ByteBuffer tdbInit();

    /** tdb_error tdb_open(tdb *tdb, const char *root) */
    private native int tdbOpen(ByteBuffer tdb, String root);

    /** void tdb_close(tdb *db) */
    private native void tdbClose(ByteBuffer db);

    /** uint64_t tdb_num_trails(const tdb *db) */
    private native long tdbNumTrails(ByteBuffer db);

    /** uint64_t tdb_num_events(const tdb *db) */
    private native long tdbNumEvents(ByteBuffer db);

    /** uint64_t tdb_num_fields(const tdb *db) */
    private native long tdbNumFields(ByteBuffer db);

    /** uint64_t tdb_min_timestamp(const tdb *db) */
    private native long tdbMinTimestamp(ByteBuffer db);

    /** uint64_t tdb_max_timestamp(const tdb *db) */
    private native long tdbMaxTimestamp(ByteBuffer db);

    /** uint64_t tdb_version(const tdb *db) */
    private native long tdbVersion(ByteBuffer db);

    /** const char *tdb_error_str(tdb_error errcode) */
    private native String tdbErrorStr(int errcode);

    // ========================================================================
    // Working with items, fields and values.
    // ========================================================================

    /** uint64_t tdb_lexicon_size(const tdb *db, tdb_field field) */
    private native long tdbLexiconSize(ByteBuffer db, long field); // tdb_field is a C uint32_t, ID of the field.

    /** tdb_error tdb_get_field(tdb *db, const char *field_name, tdb_field *field) */
    private native int tdbGetField(ByteBuffer db, String fieldName, ByteBuffer field);

    /** const char *tdb_get_field_name(tdb *db, tdb_field field) */
    private native String tdbGetFieldName(ByteBuffer db, long field);

    /** tdb_item tdb_get_item(tdb *db, tdb_field field, const char *value, uint64_t value_length) */
    private native long tdbGetItem(ByteBuffer db, long field, String value);

    /** const char *tdb_get_value(tdb *db, tdb_field field, tdb_val val, uint64_t *value_length) */
    // TODO WARNING be extremely careful here with val(uint64_t)
    private native String tdbGetValue(ByteBuffer db, long field, long val, ByteBuffer value_length);

    /** const char *tdb_get_item_value(tdb *db, tdb_item item, uint64_t *value_length) */
    private native String tdbGetItemValue(ByteBuffer db, long item, ByteBuffer value_length);

    // ========================================================================
    // Working with UUIDs.
    // ========================================================================

    /** const uint8_t *tdb_get_uuid(const tdb *db, uint64_t trail_id) */
    private native ByteBuffer tdbGetUUID(ByteBuffer db, long traildId);

    /** tdb_error tdb_get_trail_id(const tdb *db, const uint8_t uuid[16], uint64_t *trail_id) */
    private native int tdbGetTrailId(ByteBuffer db, byte[] uuid, ByteBuffer trailId);

    // ========================================================================
    // Query events with cursors.
    // ========================================================================

    /** tdb_cursor *tdb_cursor_new(const tdb *db) */
    private native ByteBuffer tdbCursorNew(ByteBuffer db); // A cursor is a void *.

    /** void tdb_cursor_free(tdb_cursor *cursor) */
    private native void tdbCursorFree(ByteBuffer cursor);

    /** tdb_error tdb_get_trail(tdb_cursor *cursor, uint64_t trail_id) */
    private native int tdbGetTrail(ByteBuffer cursor, long trailID);

    /** uint64_t tdb_get_trail_length(tdb_cursor *cursor) */
    private native long tdbGetTrailLength(ByteBuffer cursor);

    /** const tdb_event *tdb_cursor_next(tdb_cursor *cursor) */
    private native void tdbCursorNext(ByteBuffer cursor, Event event); // Fill the event in jni.

    // ========================================================================
    // Helper classes.
    // ========================================================================

    /**
     * Class allowing to easily construct a new TrailDB.
     * 
     * @author Vilya
     */
    public static class TrailDBConstructor implements Closeable {

        private TrailDBj trailDBj = TrailDBj.INSTANCE;

        /** New TrailDB output path, without .tdb. */
        private String path;

        /** Names of fields in the new TrailDB. */
        private String[] ofields;

        /** Handle to the TrailDB, returned by init method. */
        private ByteBuffer cons;

        /**
         * Construct a new TrailDB.
         * 
         * @param path TrailDB output path.
         * @param ofields Names of fields.
         * @throws NullPointerException If given path is null.
         * @throws TrailDBError If allocation fails or can not open constructor.
         */
        public TrailDBConstructor(String path, String[] ofields) {
            if (path == null) {
                throw new NullPointerException("Path must not be null.");
            }
            if (Arrays.asList(ofields).contains("")) {
                throw new IllegalArgumentException("Fields must not contain empty String.");
            }

            // Initialisation.
            this.cons = this.trailDBj.tdbConsInit();
            if (this.cons == null) {
                throw new TrailDBError("Failed to allocate memory for constructor.");
            }
            if (this.trailDBj.tdbConsOpen(this.cons, path, ofields, ofields.length) != 0) {
                throw new TrailDBError("Can not open constructor.");
            }

            this.path = path;
            this.ofields = ofields;
        }

        /**
         * Add an event to the TrailDB.
         * 
         * @param uuid UUID of the event to be added.
         * @param timestamp Event timestamp.
         * @param values Value of each field.
         * @throws TrailDBError if number of values does not match number of fields or failed to add to the DB.
         * @throws IllegalArgumentException If {@code uuid} is an invalid 32-byte hex string.
         */
        public void add(String uuid, long timestamp, String[] values) {
            int n = values.length;
            if (n != this.ofields.length) {
                // FIXME this is a hack to avoid random errors in the C lib.
                // Need to investigate add function in JNI.
                throw new TrailDBError("Number of values does not match number of fields.");
            }
            long[] value_lenghts = new long[n];
            for(int i = 0; i < n; i++) {
                value_lenghts[i] = values[i].length();
            }

            byte[] rawUUID = this.trailDBj.UUIDRaw(uuid);
            if (rawUUID == null) {
                throw new IllegalArgumentException("uuid is invalid.");
            }
            int errCode = this.trailDBj.tdbConsAdd(this.cons, rawUUID, timestamp, values, value_lenghts);
            if (errCode != 0) {
                throw new TrailDBError("Failed to add: " + errCode);
            }
        }

        /**
         * Merge an existing TrailDB to this constructor. The fields must be equal between the existing and the new
         * TrailDB.
         * 
         * @param db The db to merge to this one.
         * @throws TrailDBError if the merge fails.
         */
        public void append(TrailDB db) {
            int errCode = this.trailDBj.tdbConsAppend(this.cons, db.db);
            if (errCode != 0) {
                throw new TrailDBError("Failed to merge dbs: " + errCode);
            }
        }

        /**
         * Finalize TrailDB construction. Finalization takes care of compacting the events and creating a valid TrailDB
         * file. Events can not be added after this has been called.
         */
        public TrailDB finalise() {
            if (this.trailDBj.tdbConsFinalize(this.cons) != 0) {
                throw new TrailDBError("Failed to finalize.");
            }
            LOGGER.log(Level.INFO, "Finalisation done.");
            return new TrailDB(this.path);
        }

        @Override
        public void close() throws IOException {
            if (this.cons != null) {
                LOGGER.log(Level.INFO, "Closing TrailDB.");
                this.trailDBj.tdbConsClose(this.cons);
            }
        }
    }

    /**
     * Class used to query an existing TrailDB.
     * 
     * @author Vilya
     *
     */
    public static class TrailDB implements Closeable {

        private TrailDBj trailDBj = TrailDBj.INSTANCE;

        /** ByteBuffer holding a pointer to the traildb. */
        private ByteBuffer db;

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

            ByteBuffer db = this.trailDBj.tdbInit();
            this.db = db;

            if (this.trailDBj.tdbOpen(this.db, path) != 0) {
                throw new TrailDBError("Failed to opend db.");
            }

            this.numTrails = this.trailDBj.tdbNumTrails(db);
            this.numEvents = this.trailDBj.tdbNumEvents(db);
            this.numFields = this.trailDBj.tdbNumFields(db);
            this.fields = new ArrayList<>((int)this.numFields);

            for(int i = 0; i < this.numFields; i++) {
                this.fields.add(this.trailDBj.tdbGetFieldName(this.db, i));
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
            long min = this.trailDBj.tdbMinTimestamp(this.db);
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
            long max = this.trailDBj.tdbMaxTimestamp(this.db);
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
            long version = this.trailDBj.tdbVersion(this.db);
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
            ByteBuffer b = ByteBuffer.allocate(4);
            if (this.trailDBj.tdbGetField(this.db, fieldName, b) != 0) {
                throw new TrailDBError("Failed to retreive field. Field not found");
            }
            return b.getInt(0);
        }

        /**
         * Get the number of distinct values in the given field, +1 counting the empty string if not present.
         * 
         * @param field The field ID.
         * @return The number of distinct values.
         * @throws TrailDBError if the field index is invalid ( <=0 | > number of fields).
         */
        public long getLexiconSize(long field) {
            long value = this.trailDBj.tdbLexiconSize(this.db, field);
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
            String res = this.trailDBj.tdbGetFieldName(this.db, fieldId);
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
            long item = this.trailDBj.tdbGetItem(this.db, fieldID, value);
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
            String value = this.trailDBj.tdbGetValue(this.db, field, val, bb);
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
            String value = this.trailDBj.tdbGetItemValue(this.db, item, bb);
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

            ByteBuffer uuid = this.trailDBj.tdbGetUUID(this.db, trailID);
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
            int errCode = this.trailDBj.tdbGetTrailId(this.db, rawUUID, trailID);
            if (errCode != 0) {
                throw new TrailDBError("UUID not found. " + errCode);
            }
            long res = trailID.getLong(0);
            if (res < 0) {
                LOGGER.warning("Received trail ID overflow: " + res);
            }
            return res;
        }

        public TrailDBCursor trail(long trailID) { // Python has more params.
            ByteBuffer cursor = this.trailDBj.tdbCursorNew(this.db);
            if (cursor == null) {
                throw new TrailDBError("Memory allocation failed for cursor.");
            }
            int errCode = this.trailDBj.tdbGetTrail(cursor, trailID);
            if (errCode != 0) {
                throw new TrailDBError("Falied to create cursor: " + errCode);
            }
            Event e = new Event(this, this.fields);
            return new TrailDBCursor(cursor, e);
        }

        public Map<String, TrailDBCursor> trails() {
            Map<String, TrailDBCursor> res = new HashMap<>();
            for(int i = 0; i < this.length(); i++) {
                res.put(this.getUUID(i), this.trail(i));
            }

            return res;
        }

        @Override
        public void close() throws IOException {
            if (this.db != null) {
                this.trailDBj.tdbClose(this.db);
            }
        }
    }

    public static class TrailDBCursor {

        private ByteBuffer cursor;
        private Event event;

        public TrailDBCursor(ByteBuffer cursor, Event event) {
            this.event = event;
            this.cursor = cursor;
        }

        public Event next() {
            // ByteBuffer next = TrailDBj.INSTANCE.tdbCursorNext(this.cursor);
            // this.event.build(next);

            TrailDBj.INSTANCE.tdbCursorNext(this.cursor, this.event);
            return this.event;
        }

        @Override
        public void finalize() {
            if (this.cursor != null) {
                TrailDBj.INSTANCE.tdbCursorFree(this.cursor);
            }
        }
    }

    public static class Event {

        private TrailDB trailDB;

        private long timestamp;
        private long numItems;
        private List<Long> items; // items encoded on uint64_t.
        /** This one contains the timestamp name. */
        private List<String> fieldsNames;
        private List<String> fieldsValues;

        /**
         * The constructor just initialise the name of the fields (timestamp, field1, field2,...) and doest NOT fill
         * items.
         * 
         * @param fieldsNames
         */
        public Event(TrailDB trailDB, List<String> fieldsNames) {
            this.trailDB = trailDB;
            this.fieldsNames = fieldsNames;
        }

        public void build(long timestamp, long numItems) {
            this.timestamp = timestamp;
            this.numItems = numItems;
            this.items = new ArrayList<>((int)numItems);
            this.fieldsValues = new ArrayList<>();
        }

        public void addItem(long item) {
            this.items.add(item);
            this.fieldsValues.add(this.trailDB.getItemValue(item));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String sep = ", ";
            for(int i = 0; i < this.numItems; i++) {
                if (i == this.numItems - 1) {
                    sep = "";
                }
                // Skip the "time" in names.
                sb.append(this.fieldsNames.get(i + 1) + "=" + this.fieldsValues.get(i) + sep);
            }
            return "Event(time=" + this.timestamp + ", " + sb.toString() + ")";
        }
    }

    /**
     * Exception thrown when something bad happens while performing action on the TrailDB.
     * 
     * @author Vilya
     */
    public static class TrailDBError extends RuntimeException {

        private static final long serialVersionUID = -6086129664942253809L;

        public TrailDBError(String message) {
            super(message);
        }
    }
}
