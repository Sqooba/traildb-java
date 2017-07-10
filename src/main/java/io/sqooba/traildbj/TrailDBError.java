package io.sqooba.traildbj;

/**
 * Exception thrown when something bad happens while performing action on the TrailDB.
 * 
 * @author B. Sottas
 */
public class TrailDBError extends RuntimeException {

    private static final long serialVersionUID = -6086129664942253809L;

    public TrailDBError(String message) {
        super(message);
    }
}
