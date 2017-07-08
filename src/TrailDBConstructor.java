
package traildb;

import java.util.UUID;
import java.nio.ByteBuffer;

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

	public void add(UUID uuid, int timestamp, String[] values) {
		byte uuidBytes[] = new byte[16];
		uuidBytes = uuidToBytes(uuid);
		nativeAdd(uuidBytes, timestamp, values);
	}

	private native void nativeAdd(byte[] uuid, int timestamp, String[] values);

	private static byte[] uuidToBytes(UUID uuid) {
		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
	}

	public native void finalize();
	public native void close();
	static {
		System.loadLibrary("TrailDBConstructor");
	}
}