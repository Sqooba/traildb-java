package io.sqooba.traildbj;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public enum TrailDBj {

    INSTANCE;

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

    public static class TrailDBConstructor implements Closeable {

        private TrailDBj trailDBj = TrailDBj.INSTANCE;

        private String path;
        private String[] ofields;
        private ByteBuffer cons;

        public TrailDBConstructor(String path, String[] ofields) {
            if (path == null) {
                throw new NullPointerException("Path must not be null.");
            }

            // Initialisation.
            this.cons = trailDBj.tdbConsInit();
            if (trailDBj.tdbConsOpen(this.cons, path, ofields, ofields.length) != 0) {
                throw new RuntimeException("Can not open constructor."); // TODO
            }

            this.path = path;
            this.ofields = ofields;
        }

        public void add(String uuid, long timestamp, String[] values) {
            int n = values.length;
            long[] value_lenghts = new long[n];
            for(int i = 0; i < n; i++) {
                value_lenghts[i] = values[i].length();
            }

            if (trailDBj.tdbConsAdd(cons, uuid.getBytes(), timestamp, values, value_lenghts) != 0) {
                throw new RuntimeException("Failed to add.");
            }
        }

        public void append() {
            throw new UnsupportedOperationException("Not done yet because need existing db.");
        }

        public void finalize() {
            if (trailDBj.tdbConsFinalize(this.cons) != 0) {
                throw new RuntimeException("Failed to finalize.");
            }
        }

        @Override
        public void close() throws IOException {
            if (this.cons != null) {
                System.out.println("Closing cons.");
                trailDBj.tdbConsClose(this.cons);
            }
        }
    }
}
