package traildb;

public class TrailDBCursor {

	private long cur;

	private long db;

	public TrailDBCursor(TrailDB tdb) {
		init(tdb);
	}

	private native void init(TrailDB tdb);

	public native void free();

	public native void getTrail(long trailId);

	public native long getTrailLength();

	public native void setEventFilter(TrailDBEventFilter filter);

	public native void unsetEventFilter();

	public native TrailDBTrail next();

	public native TrailDBTrail peek();

	private static native void initIDs();
	
	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}