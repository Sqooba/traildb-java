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
        } catch(final DecoderException e) {
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
            final InputStream in = TrailDBNative.class.getResourceAsStream("/" + name);

            if (in == null) {
                throw new NullPointerException();
            }

            fileOut = File.createTempFile("traildbjava", name.substring(name.indexOf(".")));

            final OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in, out);
            LOGGER.info("Lib copied to: " + fileOut.getAbsolutePath());
            in.close();
            out.close();

            fileOut.deleteOnExit();

            System.load(fileOut.getAbsolutePath());
        } catch(final Exception e) {
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
    private native long tdbConsInit();

    /** tdb_error tdb_cons_open(tdb_const *cons, const char *root, const char **ofield_names, uint64_t num_ofields) */
    private native int tdbConsOpen(long cons, String root, String[] ofieldNames, long numOfields);

    /** void tdb_cons_close(tdb_cons *cons) */
    private native void tdbConsClose(long cons);

    /**
     * tdb_error tdb_cons_add(tdb_cons *cons,const uint8_t uuid[16],const uint64_t timestamp,const char **values,const
     * uint64_t *value_lengths)
     */
    private native int tdbConsAdd(long cons, byte[] uuid, long timestamp, String[] values,
            long[] valueLengths);

    /** tdb_error tdb_cons_append(tdb_cons *cons, const tdb *db) */
    private native int tdbConsAppend(long cons, long db);

    /** tdb_error tdb_cons_finalize(tdb_cons *cons) */
    private native int tdbConsFinalize(long cons);

    // ========================================================================
    // Open a TrailDB and access metadata.
    // ========================================================================

    /** tdb *tdb_init(void) */
    private native long tdbInit();

    /** tdb_error tdb_open(tdb *tdb, const char *root) */
    private native int tdbOpen(long tdb, String root);

    /** void tdb_close(tdb *db) */
    private native void tdbClose(long db);

    /** uint64_t tdb_num_trails(const tdb *db) */
    private native long tdbNumTrails(long db);

    /** uint64_t tdb_num_events(const tdb *db) */
    private native long tdbNumEvents(long db);

    /** uint64_t tdb_num_fields(const tdb *db) */
    private native long tdbNumFields(long db);

    /** uint64_t tdb_min_timestamp(const tdb *db) */
    private native long tdbMinTimestamp(long db);

    /** uint64_t tdb_max_timestamp(const tdb *db) */
    private native long tdbMaxTimestamp(long db);

    /** uint64_t tdb_version(const tdb *db) */
    private native long tdbVersion(long db);

    /** const char *tdb_error_str(tdb_error errcode) */
    private native String tdbErrorStr(int errcode);

    // ========================================================================
    // Working with items, fields and values.
    // ========================================================================

    /** uint64_t tdb_lexicon_size(const tdb *db, tdb_field field) */
    private native long tdbLexiconSize(long db, long field); // tdb_field is a C uint32_t, ID of the field.

    /** tdb_error tdb_get_field(tdb *db, const char *field_name, tdb_field *field) */
    private native int tdbGetField(long db, String fieldName, ByteBuffer field);

    /** const char *tdb_get_field_name(tdb *db, tdb_field field) */
    private native String tdbGetFieldName(long db, long field);

    /** tdb_item tdb_get_item(tdb *db, tdb_field field, const char *value, uint64_t value_length) */
    private native long tdbGetItem(long db, long field, String value);

    /** const char *tdb_get_value(tdb *db, tdb_field field, tdb_val val, uint64_t *value_length) */
    // TODO WARNING be extremely careful here with val(uint64_t)
    private native String tdbGetValue(long db, long field, long val, ByteBuffer value_length);

    /** const char *tdb_get_item_value(tdb *db, tdb_item item, uint64_t *value_length) */
    private native String tdbGetItemValue(long db, long item, ByteBuffer value_length);

    // ========================================================================
    // Working with UUIDs.
    // ========================================================================

    /** const uint8_t *tdb_get_uuid(const tdb *db, uint64_t trail_id) */
    private native ByteBuffer tdbGetUUID(long db, long traildId);

    /** tdb_error tdb_get_trail_id(const tdb *db, const uint8_t uuid[16], uint64_t *trail_id) */
    private native int tdbGetTrailId(long db, byte[] uuid, ByteBuffer trailId);

    // ========================================================================
    // Query events with cursors.
    // ========================================================================

    /** tdb_cursor *tdb_cursor_new(const tdb *db) */
    private native long tdbCursorNew(long db); // A cursor is a void *.

    /** void tdb_cursor_free(tdb_cursor *cursor) */
    private native void tdbCursorFree(long cursor);

    /** tdb_error tdb_get_trail(tdb_cursor *cursor, uint64_t trail_id) */
    private native int tdbGetTrail(long cursor, long trailID);

    /** uint64_t tdb_get_trail_length(tdb_cursor *cursor) */
    private native long tdbGetTrailLength(long cursor);

    /**
     * const tdb_event *tdb_cursor_next(tdb_cursor *cursor)
     *
     * @param event
     */
    private native int tdbCursorNext(long cursor, TrailDBEvent event);

    // ========================================================================
    // Custom.
    // ========================================================================

    public native String eventGetItemValue(long db, int index, TrailDBEvent event);

    @Override
    public long consInit() {
        return tdbConsInit();
    }

    @Override
    public int consOpen(long cons, String root, String[] ofieldNames, long numOfields) {
        return tdbConsOpen(cons, root, ofieldNames, numOfields);
    }

    @Override
    public void consClose(long cons) {
        tdbConsClose(cons);

    }

    @Override
    public int consAdd(long cons, byte[] uuid, long timestamp, String[] values, long[] valueLengths) {
        return tdbConsAdd(cons, uuid, timestamp, values, valueLengths);
    }

    @Override
    public int consAppend(long cons, long db) {
        return tdbConsAppend(cons, db);
    }

    @Override
    public int consFinalize(long cons) {
        return tdbConsFinalize(cons);
    }

    @Override
    public long init() {
        return tdbInit();
    }

    @Override
    public int open(long db, String root) {
        return tdbOpen(db, root);
    }

    @Override
    public void close(long db) {
        tdbClose(db);
    }

    @Override
    public long numTrails(long db) {
        return tdbNumTrails(db);
    }

    @Override
    public long numEvents(long db) {
        return tdbNumEvents(db);
    }

    @Override
    public long numFields(long db) {
        return tdbNumFields(db);
    }

    @Override
    public long minTimestamp(long db) {
        return tdbMinTimestamp(db);
    }

    @Override
    public long maxTimestamp(long db) {
        return tdbMaxTimestamp(db);
    }

    @Override
    public long version(long db) {
        return tdbVersion(db);
    }

    @Override
    public String errorStr(int errcode) {
        return tdbErrorStr(errcode);
    }

    @Override
    public long lexiconSize(long db, long field) {
        return tdbLexiconSize(db, field);
    }

    @Override
    public int getField(long db, String fieldName, ByteBuffer field) {
        return tdbGetField(db, fieldName, field);
    }

    @Override
    public String getFieldName(long db, long field) {
        return tdbGetFieldName(db, field);
    }

    @Override
    public long getItem(long db, long field, String value) {
        return tdbGetItem(db, field, value);
    }

    @Override
    public String getValue(long db, long field, long val, ByteBuffer value_length) {
        return tdbGetValue(db, field, val, value_length);
    }

    @Override
    public String getItemValue(long db, long item, ByteBuffer value_length) {
        return tdbGetItemValue(db, item, value_length);
    }

    @Override
    public ByteBuffer getUUID(long db, long traildId) {
        return tdbGetUUID(db, traildId);
    }

    @Override
    public int getTrailId(long db, byte[] uuid, ByteBuffer trailId) {
        return tdbGetTrailId(db, uuid, trailId);
    }

    @Override
    public long cursorNew(long db) {
        return tdbCursorNew(db);
    }

    @Override
    public void cursorFree(long cursor) {
        tdbCursorFree(cursor);
    }

    @Override
    public int getTrail(long cursor, long trailID) {
        return tdbGetTrail(cursor, trailID);
    }

    @Override
    public long getTrailLength(long cursor) {
        return tdbGetTrailLength(cursor);
    }

    @Override
    public int cursorNext(long cursor, TrailDBEvent event) {
        return tdbCursorNext(cursor, event);
    }
}
