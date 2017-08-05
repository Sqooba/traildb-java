package traildb;

public class TrailDBTrail
{
	private long timestamp;

	private long numItems;

	private long items;

	private long db;

	private long cur;

	public TrailDBTrail(TrailDB tdb, long trailId) {
		init(tdb, trailId);
	}

	private native void init(TrailDB tdb, long trailId);

	/**
	 * Get item i that trail is cursor is currently pointing at
	 * @param i index of item to get
	 * @return The item value
	 */
	public String getItem(int i) {
		if (i >= numItems || i < 0) {
			throw new IndexOutOfBoundsException("getItem(" + i + ") but numItems in event is" + numItems);
		}
		return native_getItem(i);
	}

	private native String native_getItem(int i);


	/**
	 * Set the cursor to a new trailId
	 * @param trailId
	 */
	public native void getTrail(long trailId);

	/**
	 * Get the length of the trail. This exhausts the trail
	 * @return
	 */
	public native long getTrailLength();

	public native void setEventFilter(TrailDBEventFilter filter);

	public native void unsetEventFilter();

	public native TrailDBTrail next();

	public native TrailDBTrail peek();

	public long getTimestamp() {
		if (items == 0) {
			throw new IllegalStateException("Cursor is not pointing at an event");
		}
		return timestamp;
	}

	public long getNumItems() {
		if (items == 0) {
			throw new IllegalStateException("Cursor is not pointing at an event");
		}
		return numItems;
	}

	private static native void initIDs();

	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}
