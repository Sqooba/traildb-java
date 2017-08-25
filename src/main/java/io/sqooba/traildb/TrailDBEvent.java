package io.sqooba.traildb;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * An event in the trail database.
 *
 * @author B. Sottas
 *
 */
public class TrailDBEvent {

    /**
     * Reference to the TrailDB instance that initially created a cursor containing this event.
     */
    private TrailDB trailDB;

    private long timestamp;
    private long numItems;
    private long[] items;

    private long eventStruct;

    /** This one contains the timestamp name. */
    private String[] fieldNames;

    protected TrailDBEvent(TrailDB trailDB, String[] fieldsNames) {
        this.trailDB = trailDB;
        this.fieldNames = fieldsNames;
    }

    protected TrailDBEvent() {}

    /**
     * Get the timestamp of this event.
     *
     * @return The timestamp of this event.
     */
    public long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Get the number of items in this event.
     *
     * @return The number of items in this event.
     */
    public long getNumItems() {
        return this.numItems;
    }

    /**
     * Get the fields names of this event. Contains the timestamp name.
     *
     * @return The fields names of this event.
     */
    public String[] getFieldNames() {
        return Arrays.copyOf(this.fieldNames, this.fieldNames.length);
    }

    /**
     * Get the decoded value of an item.
     *
     * @param index The item index.
     * @return The decoded item as a String.
     */
    public String getFieldValue(int index) {
        // Caching possibility here to avoid going back to JNI to decode items.
        final ByteBuffer bb = ByteBuffer.allocate(8);
        final String value = TrailDBNative.INSTANCE.eventGetItemValue(this.trailDB.db, index, bb);
        if (value == null) {
            throw new TrailDBException("Value not found.");
        }
        final long value_length = bb.getLong(0);
        if (value_length > Integer.MAX_VALUE) {
            throw new TrailDBException(
                    "Overflow, received a String value that is larger than the java String capacity.");
        }
        return value.substring(0, (int)value_length);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        String sep = ", ";
        for(int i = 0; i < this.numItems; i++) {
            if (i == this.numItems - 1) {
                sep = "";
            }
            // Skip the "time" in names.
            sb.append(this.fieldNames[i + 1] + "=" + this.getFieldValue(i) + sep);
        }
        return "Event(time=" + this.timestamp + ", " + sb.toString() + ")";
    }

}
