package traildb;

public class TrailDBMultiCursor {

	private long cur;

	public TrailDBMultiCursor(TrailDBCursor[] cursors) {
		init(cursors);
	}
	private native void init(TrailDBCursor[] cursors);

	public native void free();

	public native void reset();

	public native TrailDBMultiTrail next();

	public native TrailDBMultiTrail[] nextBatch(int maxEvents);
	
	public native TrailDBMultiTrail peek();

	private static native void initIDs();

	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}