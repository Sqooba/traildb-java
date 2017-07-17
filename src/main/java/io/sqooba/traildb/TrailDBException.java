package io.sqooba.traildb;

/**
 * Exception thrown when something bad happens while performing action on the TrailDB.
 *
 * @author B. Sottas
 */
public class TrailDBException extends RuntimeException {

    private static final long serialVersionUID = -6086129664942253809L;

    public TrailDBException(String message) {
        super(message);
    }
}
