package io.sqooba.traildb;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to perform native call to the TrailDB C library. Base on the available Python bindings.
 * 
 * @author B. Sottas
 *
 */
public enum TrailDBNative implements TrailDBInterface {

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
            LOGGER.error("Failed to convert hexstring to string.", e);
        }
        return b;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TrailDBNative.class.getName());

    static {
        loadLib("traildbjava");
    }

    /**
     * Extract the library from the jar/project, copy it outside so we can load it because this is not possible from
     * inside the jar.
     *
     * @param name The name of the library, without prefix/suffix.
     */
    private static void loadLib(String name) {
        name = System.mapLibraryName(name);
        File fileOut = null;
        try {
            InputStream in = TrailDBNative.class.getResourceAsStream("/" + name);

            if (in == null) {
                throw new NullPointerException();
            }

            fileOut = File.createTempFile("traildbjava", name.substring(name.indexOf(".")));

            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            LOGGER.info("Lib copied to: " + fileOut.getAbsolutePath());
            in.close();
            out.close();

            fileOut.deleteOnExit();

            System.load(fileOut.getAbsolutePath());
        } catch(Exception e) {
            LOGGER.error("Failed to load library.", e);
            System.exit(-1);
        } finally {
            // This part is nearly impossible to write good tests on because of the bytecode representation of finally
            // blocks. Indeed, instructions in finally are duplicated many many times and some branches are not
            // reachable if we catch a general Exception.
            // See https://stackoverflow.com/questions/32280087/code-coverage-finally-block for example.
            if (fileOut != null) {
                fileOut.delete();
            }
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
    private native TrailDBEvent tdbCursorNext(ByteBuffer cursor);

    @Override
    public ByteBuffer consInit() {
        return tdbConsInit();
    }

    @Override
    public int consOpen(ByteBuffer cons, String root, String[] ofieldNames, long numOfields) {
        return tdbConsOpen(cons, root, ofieldNames, numOfields);
    }

    @Override
    public void consClose(ByteBuffer cons) {
        tdbConsClose(cons);

    }

    @Override
    public int consAdd(ByteBuffer cons, byte[] uuid, long timestamp, String[] values, long[] valueLengths) {
        return tdbConsAdd(cons, uuid, timestamp, values, valueLengths);
    }

    @Override
    public int consAppend(ByteBuffer cons, ByteBuffer db) {
        return tdbConsAppend(cons, db);
    }

    @Override
    public int consFinalize(ByteBuffer cons) {
        return tdbConsFinalize(cons);
    }

    @Override
    public ByteBuffer init() {
        return tdbInit();
    }

    @Override
    public int open(ByteBuffer tdb, String root) {
        return tdbOpen(tdb, root);
    }

    @Override
    public void close(ByteBuffer db) {
        tdbClose(db);
    }

    @Override
    public long numTrails(ByteBuffer db) {
        return tdbNumTrails(db);
    }

    @Override
    public long numEvents(ByteBuffer db) {
        return tdbNumEvents(db);
    }

    @Override
    public long numFields(ByteBuffer db) {
        return tdbNumFields(db);
    }

    @Override
    public long minTimestamp(ByteBuffer db) {
        return tdbMinTimestamp(db);
    }

    @Override
    public long maxTimestamp(ByteBuffer db) {
        return tdbMaxTimestamp(db);
    }

    @Override
    public long version(ByteBuffer db) {
        return tdbVersion(db);
    }

    @Override
    public String errorStr(int errcode) {
        return tdbErrorStr(errcode);
    }

    @Override
    public long lexiconSize(ByteBuffer db, long field) {
        return tdbLexiconSize(db, field);
    }

    @Override
    public int getField(ByteBuffer db, String fieldName, ByteBuffer field) {
        return tdbGetField(db, fieldName, field);
    }

    @Override
    public String getFieldName(ByteBuffer db, long field) {
        return tdbGetFieldName(db, field);
    }

    @Override
    public long getItem(ByteBuffer db, long field, String value) {
        return tdbGetItem(db, field, value);
    }

    @Override
    public String getValue(ByteBuffer db, long field, long val, ByteBuffer value_length) {
        return tdbGetValue(db, field, val, value_length);
    }

    @Override
    public String getItemValue(ByteBuffer db, long item, ByteBuffer value_length) {
        return tdbGetItemValue(db, item, value_length);
    }

    @Override
    public ByteBuffer getUUID(ByteBuffer db, long traildId) {
        return tdbGetUUID(db, traildId);
    }

    @Override
    public int getTrailId(ByteBuffer db, byte[] uuid, ByteBuffer trailId) {
        return tdbGetTrailId(db, uuid, trailId);
    }

    @Override
    public ByteBuffer cursorNew(ByteBuffer db) {
        return tdbCursorNew(db);
    }

    @Override
    public void cursorFree(ByteBuffer cursor) {
        tdbCursorFree(cursor);
    }

    @Override
    public int getTrail(ByteBuffer cursor, long trailID) {
        return tdbGetTrail(cursor, trailID);
    }

    @Override
    public long getTrailLength(ByteBuffer cursor) {
        return tdbGetTrailLength(cursor);
    }

    @Override
    public TrailDBEvent cursorNext(ByteBuffer cursor) {
        return tdbCursorNext(cursor);
    }
}
