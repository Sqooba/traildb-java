package traildb;

import traildb.filters.TrailDBEventFilter;

public class TrailDBTrail
{
	private long timestamp;

	private long numItems;

	private long items;

	private long db;

	private long cur;

	private long currentTrail;

	private long numTrails;

	public TrailDBTrail(TrailDB tdb, long trailId) {
		currentTrail = trailId;
		numTrails = tdb.numTrails();
		init(tdb, trailId);
	}

	private native void init(TrailDB tdb, long trailId);

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

	public String[] getItems() {
		String[] output = new String[(int) numItems];
		for (long i = 0; i < numItems; i++) {
			output[(int) i] = getItem((int) i);
		}
		return output;
	}

	/**
	 * Set the cursor to a new trailId
	 * @param trailId
	 */
	public void getTrail(long trailId) {
		currentTrail = trailId;
		native_getTrail(trailId);
	}

	private native void native_getTrail(long trailId);

	/**
	 * Set the cursor to the next trail.
	 * This is useful for processing all trails
	 * eg.
	 * do {
	 *   <process trail>
	 * } while (trail.nextTrail());
	 *
	 * @return succeded in getting another trail
	 */
	public boolean nextTrail() {
		currentTrail++;
		if (currentTrail >= numTrails) {
			return false;
		}
		native_getTrail(currentTrail);
		return true;
	}

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

	/**
	 * Get number of items the event has
	 * @return number of items
	 */
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
