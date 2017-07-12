package io.sqooba.traildbj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An event in the trail database.
 * 
 * @author B. Sottas
 *
 */
public class TrailDBEvent {

    /** Reference to the TrailDB instance that initially created a cursor containing this event. */
    private TrailDB trailDB;

    private long timestamp;
    private long numItems;
    private List<Long> items; // items encoded on uint64_t.

    /** This one contains the timestamp name. */
    private List<String> fieldNames;

    /** Does NOT contain the timestamp value. */
    private List<String> fieldValues;

    /** Indicates if a tdb_cursor_next as already been called once or not. */
    private boolean built = false;

    /**
     * The constructor just initialise the name of the fields (timestamp, field1, field2,...) and doest NOT fill items.
     * 
     * @param fieldsNames Names of the fields.
     */
    protected TrailDBEvent(TrailDB trailDB, List<String> fieldsNames) {
        this.trailDB = trailDB;
        this.fieldNames = fieldsNames;
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
    public List<String> getFieldNames() {
        return Collections.unmodifiableList(this.fieldNames);
    }

    /**
     * Get the fields values of this event. Does NOT contain the timestamp value.
     * 
     * @return The fields values of this event.
     */
    public List<String> getFieldsValues() {
        return Collections.unmodifiableList(this.fieldValues);
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
            sb.append(this.fieldNames.get(i + 1) + "=" + this.fieldValues.get(i) + sep);
        }
        return "Event(time=" + this.timestamp + ", " + sb.toString() + ")";
    }

    /**
     * This method is called by the c++ code to initialise this event.
     * 
     * @param timestamp The event timestamp.
     * @param numItems The number of items in this event.
     */
    protected void build(long timestamp, long numItems) {
        this.timestamp = timestamp;
        this.numItems = numItems;
        this.items = new ArrayList<>((int)numItems);
        this.fieldValues = new ArrayList<>();
        this.built = true;
    }

    /**
     * This method is called by the c++ code to add an item in this event.
     * 
     * @param item The item to be added.
     */
    protected void addItem(long item) {
        this.items.add(item);
        this.fieldValues.add(this.trailDB.getItemValue(item));
    }

    /**
     * Indicates if a next() call has be performed at least once on the iterator.
     * 
     * @return true if a next() has been called.
     */
    protected boolean isBuilt() {
        return this.built;
    }
}
