package io.sqooba.traildb;

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
    private long items2;

    /** This one contains the timestamp name. */
    private String[] fieldNames;
    private String[] fieldValues;

//    protected TrailDBEvent(long timestamp, long numItems, long[] items) {
//        this.timestamp = timestamp;
//        this.numItems = numItems;
//        this.items = Arrays.copyOf(items, (int)numItems);
//    }

    protected TrailDBEvent(TrailDB trailDB, String[] fieldsNames) {
        this.trailDB = trailDB;
        this.fieldNames = fieldsNames;
        this.fieldValues = new String[fieldNames.length - 1];
    }

    protected TrailDBEvent() {
    }

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
        return this.trailDB.getItemValue(items[index]);
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
            sb.append(this.fieldNames[i + 1] + "=" + this.getFieldValue(i) + sep);
        }
        return "Event(time=" + this.timestamp + ", " + sb.toString() + ")";
    }

}
