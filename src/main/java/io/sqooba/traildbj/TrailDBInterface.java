package io.sqooba.traildbj;

import java.nio.ByteBuffer;

public interface TrailDBInterface {

    public ByteBuffer consInit();

    public int consOpen(ByteBuffer cons, String root, String[] ofieldNames, long numOfields);

    public void consClose(ByteBuffer cons);

    public int consAdd(ByteBuffer cons, byte[] uuid, long timestamp, String[] values,
            long[] valueLengths);

    public int consAppend(ByteBuffer cons, ByteBuffer db);

    public int consFinalize(ByteBuffer cons);

    // ========================================================================
    // Open a TrailDB and access metadata.
    // ========================================================================

    public ByteBuffer init();

    public int open(ByteBuffer db, String root);

    public void close(ByteBuffer db);

    public long numTrails(ByteBuffer db);

    public long numEvents(ByteBuffer db);

    public long numFields(ByteBuffer db);

    public long minTimestamp(ByteBuffer db);

    public long maxTimestamp(ByteBuffer db);

    public long version(ByteBuffer db);

    public String errorStr(int errcode);

    // ========================================================================
    // Working with items, fields and values.
    // ========================================================================

    public long lexiconSize(ByteBuffer db, long field);

    public int getField(ByteBuffer db, String fieldName, ByteBuffer field);

    public String getFieldName(ByteBuffer db, long field);

    public long getItem(ByteBuffer db, long field, String value);

    public String getValue(ByteBuffer db, long field, long val, ByteBuffer value_length);

    public String getItemValue(ByteBuffer db, long item, ByteBuffer value_length);

    // ========================================================================
    // Working with UUIDs.
    // ========================================================================

    public ByteBuffer getUUID(ByteBuffer db, long traildId);

    public int getTrailId(ByteBuffer db, byte[] uuid, ByteBuffer trailId);

    // ========================================================================
    // Query events with cursors.
    // ========================================================================

    public ByteBuffer cursorNew(ByteBuffer db); // A cursor is a void *.

    public void cursorFree(ByteBuffer cursor);

    public int getTrail(ByteBuffer cursor, long trailID);

    public long getTrailLength(ByteBuffer cursor);

    public int cursorNext(ByteBuffer cursor, TrailDBEvent event); // Fill the event in jni.
}
