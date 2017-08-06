package traildb;

public class TrailDBMultiTrail {
	public long cursorIndex;

	private long timestamp;

	private long numItems;

	private long items;

	private long cur;

	private long db;

	public TrailDBMultiTrail(TrailDBTrail[] trails) {
		init(trails);
	}

	private native void init(TrailDBTrail[] trails);

	/**
	 * Get item i that trail cursor is currently pointing at
	 * @param i index of item to get
	 * @return The item value
	 */
	public String getItem(int i) {
		if (i >= numItems || i < 0) {
			throw new IndexOutOfBoundsException("getItem(" + i + ") but numItems in event is " + numItems);
		}
		return native_getItem(i);
	}

	private native String native_getItem(int i);

	public native void free();

	public native void reset();

	public native TrailDBMultiTrail next();

	public native TrailDBMultiTrail[] nextBatch(int maxEvents);

	public native TrailDBMultiTrail peek();

	public long getTimestamp() {
		if (items == 0) {
			// throw new IllegalStateException("Cursor is not pointing at an event");
		}
		return timestamp;
	}

	public long getNumItems() {
		if (items == 0) {
			// throw new IllegalStateException("Cursor is not pointing at an event");
		}
		return numItems;
	}

	private static native void initIDs();

	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}
