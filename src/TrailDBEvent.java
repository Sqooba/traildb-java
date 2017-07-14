package traildb;

import traildb.TrailDBItem;

public class TrailDBEvent {
	public long timestamp;
	public long numItems;

	private long items; // Points to beginning of items
	private long db;

	public native String getItem(int i);

	static {
		System.loadLibrary("TrailDBEvent");
	}
}
