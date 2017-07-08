
package traildb;

import java.util.UUID;

public class TrailDBConstructor {
	private Object cons;
	public TrailDBConstructor(String root, String[] fields) {
		init(root, fields);
	}
	private native void init(String root, String[] fields);
	public native void add(UUID uuid, int timestamp, String[] values);
	public native void finalize();
	public native void close();
	static {
		System.loadLibrary("TrailDBConstructor");
	}
}