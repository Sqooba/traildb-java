
package traildb;

import java.util.UUID;

public class TrailDBConstructor {
	public TrailDBConstructor(String name, String[] fields) {
		System.loadLibrary("TrailDBConstructor");
		init(name, fields);
	}
	private native void init(String name, String[] fields);
	public native void add(UUID uuid, int timestamp, String[] values);
	public native void finalize();
	public native void close();
}