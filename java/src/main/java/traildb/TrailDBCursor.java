package traildb;

import traildb.TrailDBEventFilter;
import traildb.TrailDBEvent;

public class TrailDBCursor {

	private long cur;

	private long db;

	public native void free();

	public native void getTrail(long trailId);

	public native long getTrailLength();

	public native void setEventFilter(TrailDBEventFilter filter);

	public native void unsetEventFilter();

	public native TrailDBEvent next();

	public native TrailDBEvent peek();

	private static native void initIDs();
	
	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}