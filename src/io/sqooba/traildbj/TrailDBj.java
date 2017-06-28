package io.sqooba.traildbj;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to perform native call to the TrailDB C library.
 * 
 * @author Vilya
 *
 */
public enum TrailDBj {

    INSTANCE;

    private static final Logger LOGGER = Logger.getLogger(TrailDBj.class.getName());

    static {
        System.load("/home/osboxes/TrailDBj/src/io/sqooba/traildbj/libtest.so");
    }

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
         */
        public TrailDBConstructor(String path, String[] ofields) {
            if (path == null) {
                throw new NullPointerException("Path must not be null.");
            }

            // Initialisation.
            this.cons = trailDBj.tdbConsInit();
            if (trailDBj.tdbConsOpen(this.cons, path, ofields, ofields.length) != 0) {
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
         */
        public void add(String uuid, long timestamp, String[] values) {
            int n = values.length;
            long[] value_lenghts = new long[n];
            for(int i = 0; i < n; i++) {
                value_lenghts[i] = values[i].length();
            }

            if (trailDBj.tdbConsAdd(cons, uuid.getBytes(), timestamp, values, value_lenghts) != 0) {
                throw new TrailDBError("Failed to add.");
            }
        }

        public void append() {
            throw new UnsupportedOperationException("Not done yet because need existing db.");
        }

        /**
         * Finalize TrailDB construction. Finalization takes care of compacting the events and creating a valid TrailDB
         * file. Events can not be added after this has been called.
         */
        public void finalise() {
            if (trailDBj.tdbConsFinalize(this.cons) != 0) {
                LOGGER.log(Level.INFO, "Finalisation done.");
                throw new TrailDBError("Failed to finalize.");
            }
        }

        @Override
        public void close() throws IOException {
            if (this.cons != null) {
                LOGGER.log(Level.INFO, "Closing TrailDB.");
                trailDBj.tdbConsClose(this.cons);
            }
        }
    }

    /**
     * Exception thrown when something bad happens while performing action on the TrailDB.
     * 
     * @author Vilya
     */
    private static class TrailDBError extends RuntimeException {

        private static final long serialVersionUID = -6086129664942253809L;

        public TrailDBError(String message) {
            super(message);
        }
    }
}
