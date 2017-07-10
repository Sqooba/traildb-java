package traildb;

import traildb.TrailDBMultiEvent;

public class TrailDBMultiCursor {

	public native void free();

	public native void reset();

	public native TrailDBMultiEvent next();

	public native TrailDBMultiEvent[] nextBatch(int maxEvents);
	
	public native TrailDBMultiEvent peek();
}