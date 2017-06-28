package io.sqooba.traildbj;

import java.nio.ByteBuffer;
import java.util.Vector;

public class TrailDBj {
    
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
    private native int tdbConsAdd(ByteBuffer cons, byte[] uuid, long timestamp, String[] values, ByteBuffer valueLengths);
    
    /** tdb_error tdb_cons_append(tdb_cons *cons, const tdb *db) */
    private native int tdbConsAppend(ByteBuffer cons, ByteBuffer db);
    
    /** tdb_error tdb_cons_finalize(tdb_cons *cons) */
    private native int tdbConsFinalize(ByteBuffer cons);
    
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        final String[] libraries = TrailDBj.getLoadedLibraries(ClassLoader.getSystemClassLoader());
        for (String string : libraries) {
            System.out.println(string);
        }
        new TrailDBj().tdbConsInit();
    }
    private static java.lang.reflect.Field LIBRARIES = null;
    static {
        try {
            LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        LIBRARIES.setAccessible(true);
    }
    public static String[] getLoadedLibraries(final ClassLoader loader)
            throws IllegalArgumentException, IllegalAccessException {
        final Vector<String> libraries = (Vector<String>) LIBRARIES.get(loader);
        return libraries.toArray(new String[] {});
    }
}
