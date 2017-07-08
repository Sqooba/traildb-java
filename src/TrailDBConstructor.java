
package traildb;

import java.util.UUID;

public class TrailDBConstructor {
	// https://rkennke.wordpress.com/2007/07/20/efficient-jni-programming-part-i/
	// https://rkennke.wordpress.com/2007/07/24/efficient-jni-programming-ii-field-and-method-access/
	// https://rkennke.wordpress.com/2007/07/28/efficient-jni-programming-iii-array-access/
	// https://rkennke.wordpress.com/2007/07/30/efficient-jni-programming-iv-wrapping-native-data-objects/

	private long cons;
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