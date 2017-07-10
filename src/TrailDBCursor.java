package traildb;

import traildb.TrailDBEventFilter;
import traildb.TrailDBEvent;

public class TrailDBCursor {

	private long cur;

	public native void free();

	public native void getTrail(int trailId);

	public native int getTrailLength();

	public native void setEventFilter(TrailDBEventFilter filter);

	public native void unsetEventFilter();

	public native TrailDBEvent next();

	public native TrailDBEvent peek();
	
}