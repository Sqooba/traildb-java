package io.sqooba.traildbj;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * This class is used to perform native call to the TrailDB C library. Base on the available Python bindings.
 * 
 * @author B. Sottas
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
    native ByteBuffer tdbConsInit();

    /** tdb_error tdb_cons_open(tdb_const *cons, const char *root, const char **ofield_names, uint64_t num_ofields) */
    native int tdbConsOpen(ByteBuffer cons, String root, String[] ofieldNames, long numOfields);

    /** void tdb_cons_close(tdb_cons *cons) */
    native void tdbConsClose(ByteBuffer cons);

    /**
     * tdb_error tdb_cons_add(tdb_cons *cons,const uint8_t uuid[16],const uint64_t timestamp,const char **values,const
     * uint64_t *value_lengths)
     */
    native int tdbConsAdd(ByteBuffer cons, byte[] uuid, long timestamp, String[] values,
            long[] valueLengths);

    /** tdb_error tdb_cons_append(tdb_cons *cons, const tdb *db) */
    native int tdbConsAppend(ByteBuffer cons, ByteBuffer db);

    /** tdb_error tdb_cons_finalize(tdb_cons *cons) */
    native int tdbConsFinalize(ByteBuffer cons);

    // ========================================================================
    // Open a TrailDB and access metadata.
    // ========================================================================

    /** tdb *tdb_init(void) */
    native ByteBuffer tdbInit();

    /** tdb_error tdb_open(tdb *tdb, const char *root) */
    native int tdbOpen(ByteBuffer tdb, String root);

    /** void tdb_close(tdb *db) */
    native void tdbClose(ByteBuffer db);

    /** uint64_t tdb_num_trails(const tdb *db) */
    native long tdbNumTrails(ByteBuffer db);

    /** uint64_t tdb_num_events(const tdb *db) */
    native long tdbNumEvents(ByteBuffer db);

    /** uint64_t tdb_num_fields(const tdb *db) */
    native long tdbNumFields(ByteBuffer db);

    /** uint64_t tdb_min_timestamp(const tdb *db) */
    native long tdbMinTimestamp(ByteBuffer db);

    /** uint64_t tdb_max_timestamp(const tdb *db) */
    native long tdbMaxTimestamp(ByteBuffer db);

    /** uint64_t tdb_version(const tdb *db) */
    native long tdbVersion(ByteBuffer db);

    /** const char *tdb_error_str(tdb_error errcode) */
    native String tdbErrorStr(int errcode);

    // ========================================================================
    // Working with items, fields and values.
    // ========================================================================

    /** uint64_t tdb_lexicon_size(const tdb *db, tdb_field field) */
    native long tdbLexiconSize(ByteBuffer db, long field); // tdb_field is a C uint32_t, ID of the field.

    /** tdb_error tdb_get_field(tdb *db, const char *field_name, tdb_field *field) */
    native int tdbGetField(ByteBuffer db, String fieldName, ByteBuffer field);

    /** const char *tdb_get_field_name(tdb *db, tdb_field field) */
    native String tdbGetFieldName(ByteBuffer db, long field);

    /** tdb_item tdb_get_item(tdb *db, tdb_field field, const char *value, uint64_t value_length) */
    native long tdbGetItem(ByteBuffer db, long field, String value);

    /** const char *tdb_get_value(tdb *db, tdb_field field, tdb_val val, uint64_t *value_length) */
    // TODO WARNING be extremely careful here with val(uint64_t)
    native String tdbGetValue(ByteBuffer db, long field, long val, ByteBuffer value_length);

    /** const char *tdb_get_item_value(tdb *db, tdb_item item, uint64_t *value_length) */
    native String tdbGetItemValue(ByteBuffer db, long item, ByteBuffer value_length);

    // ========================================================================
    // Working with UUIDs.
    // ========================================================================

    /** const uint8_t *tdb_get_uuid(const tdb *db, uint64_t trail_id) */
    native ByteBuffer tdbGetUUID(ByteBuffer db, long traildId);

    /** tdb_error tdb_get_trail_id(const tdb *db, const uint8_t uuid[16], uint64_t *trail_id) */
    native int tdbGetTrailId(ByteBuffer db, byte[] uuid, ByteBuffer trailId);

    // ========================================================================
    // Query events with cursors.
    // ========================================================================

    /** tdb_cursor *tdb_cursor_new(const tdb *db) */
    native ByteBuffer tdbCursorNew(ByteBuffer db); // A cursor is a void *.

    /** void tdb_cursor_free(tdb_cursor *cursor) */
    native void tdbCursorFree(ByteBuffer cursor);

    /** tdb_error tdb_get_trail(tdb_cursor *cursor, uint64_t trail_id) */
    native int tdbGetTrail(ByteBuffer cursor, long trailID);

    /** uint64_t tdb_get_trail_length(tdb_cursor *cursor) */
    native long tdbGetTrailLength(ByteBuffer cursor);

    /** const tdb_event *tdb_cursor_next(tdb_cursor *cursor) */
    native int tdbCursorNext(ByteBuffer cursor, TrailDBEvent event); // Fill the event in jni.
}
