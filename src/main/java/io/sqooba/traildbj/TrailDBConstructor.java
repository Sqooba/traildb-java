package io.sqooba.traildbj;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class allowing to easily construct a new TrailDB.
 * 
 * @author B. Sottas
 */
public class TrailDBConstructor {

    private static final Logger LOGGER = Logger.getLogger(TrailDBConstructor.class.getName());

    private TrailDBj trailDBj = TrailDBj.INSTANCE;

    /** New TrailDB output path, without .tdb. */
    private String path;

    /** Names of fields in the new TrailDB. */
    private String[] ofields;

    /** Handle to the TrailDB, returned by init method. */
    private ByteBuffer cons;

    private boolean closed = false;

    /**
     * Construct a new TrailDB.
     * 
     * @param path TrailDB output path.
     * @param ofields Names of fields.
     * @throws NullPointerException If given path is null.
     * @throws TrailDBError If allocation fails or can not open constructor.
     */
    public TrailDBConstructor(String path, String[] ofields) {
        if (path == null) {
            throw new NullPointerException("Path must not be null.");
        }
        if (Arrays.asList(ofields).contains("")) {
            throw new IllegalArgumentException("Fields must not contain empty String.");
        }

        // Initialisation.
        this.cons = this.trailDBj.tdbConsInit();
        if (this.cons == null) {
            throw new TrailDBError("Failed to allocate memory for constructor.");
        }
        if (this.trailDBj.tdbConsOpen(this.cons, path, ofields, ofields.length) != 0) {
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
     * @throws TrailDBError if number of values does not match number of fields or failed to add to the DB.
     * @throws IllegalArgumentException If {@code uuid} is an invalid 32-byte hex string.
     */
    public void add(String uuid, long timestamp, String[] values) {
        if (this.closed) {
            throw new TrailDBError("Trying to add event to an already finalised database.");
        }
        int n = values.length;
        if (n != this.ofields.length) {
            // FIXME this is a hack to avoid random errors in the C lib.
            // Need to investigate add function in JNI.
            throw new TrailDBError("Number of values does not match number of fields.");
        }
        long[] value_lenghts = new long[n];
        for(int i = 0; i < n; i++) {
            value_lenghts[i] = values[i].length();
        }

        byte[] rawUUID = this.trailDBj.UUIDRaw(uuid);
        if (rawUUID == null) {
            throw new IllegalArgumentException("uuid is invalid.");
        }
        int errCode = this.trailDBj.tdbConsAdd(this.cons, rawUUID, timestamp, values, value_lenghts);
        if (errCode != 0) {
            throw new TrailDBError("Failed to add: " + errCode);
        }
    }

    /**
     * Merge an existing TrailDB to this constructor. The fields must be equal between the existing and the new TrailDB.
     * 
     * @param db The db to merge to this one.
     * @throws TrailDBError if the merge fails.
     */
    public void append(TrailDB db) {
        int errCode = this.trailDBj.tdbConsAppend(this.cons, db.db);
        if (errCode != 0) {
            throw new TrailDBError("Failed to merge dbs: " + errCode);
        }
    }

    /**
     * Finalize TrailDB construction. Finalization takes care of compacting the events and creating a valid TrailDB
     * file. Events can not be added after this has been called.
     */
    public TrailDB finalise() {
        if (this.trailDBj.tdbConsFinalize(this.cons) != 0) {
            throw new TrailDBError("Failed to finalize.");
        }
        LOGGER.log(Level.INFO, "Finalisation done.");
        this.closed = true;
        return new TrailDB(this.path);
    }

    @Override
    protected void finalize() {
        if (this.cons != null) {
            LOGGER.log(Level.INFO, "Closing TrailDB.");
            this.trailDBj.tdbConsClose(this.cons);
        }
    }
}
