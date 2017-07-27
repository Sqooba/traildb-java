package traildb;

import traildb.TrailDBMultiEvent;

public class TrailDBMultiCursor {

	private long cur;

	public TrailDBMultiCursor(TrailDBCursor[] cursors) {
		init(cursors);
	}
	private native void init(TrailDBCursor[] cursors);

	public native void free();

	public native void reset();

	public native TrailDBMultiEvent next();

	public native TrailDBMultiEvent[] nextBatch(int maxEvents);
	
	public native TrailDBMultiEvent peek();

	private static native void initIDs();

	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}