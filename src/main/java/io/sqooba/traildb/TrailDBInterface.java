package io.sqooba.traildb;

import java.nio.ByteBuffer;

public interface TrailDBInterface {

    public long consInit();

    public int consOpen(long cons, String root, String[] ofieldNames, long numOfields);

    public void consClose(long cons);

    public int consAdd(long cons, byte[] uuid, long timestamp, String[] values,
            long[] valueLengths);

    public int consAppend(long cons, long db);

    public int consFinalize(long cons);

    // ========================================================================
    // Open a TrailDB and access metadata.
    // ========================================================================

    public long init();

    public int open(long db, String root);

    public void close(long db);

    public long numTrails(long db);

    public long numEvents(long db);

    public long numFields(long db);

    public long minTimestamp(long db);

    public long maxTimestamp(long db);

    public long version(long db);

    public String errorStr(int errcode);

    // ========================================================================
    // Working with items, fields and values.
    // ========================================================================

    public long lexiconSize(long db, long field);

    public int getField(long db, String fieldName, ByteBuffer field);

    public String getFieldName(long db, long field);

    public long getItem(long db, long field, String value);

    public String getValue(long db, long field, long val, ByteBuffer value_length);

    public String getItemValue(long db, long item, ByteBuffer value_length);

    // ========================================================================
    // Working with UUIDs.
    // ========================================================================

    public ByteBuffer getUUID(long db, long traildId);

    public int getTrailId(long db, byte[] uuid, ByteBuffer trailId);

    // ========================================================================
    // Query events with cursors.
    // ========================================================================

    public long cursorNew(long db); // A cursor is a void *.

    public void cursorFree(long cursor);

    public int getTrail(long cursor, long trailID);

    public long getTrailLength(long cursor);

    public int cursorNext(long cursor, TrailDBEvent event);
}
