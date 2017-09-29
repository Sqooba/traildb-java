
package traildb;

import traildb.TrailDB;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.io.FileNotFoundException;


public class TrailDBConstructor {
	private long cons;

	public enum TDB_OPT_CONS_KEY {
		TDB_OPT_CONS_OUTPUT_FORMAT,
		TDB_OPT_CONS_NO_BIGRAMS,
	}
	public enum TDB_OPT_CONS_VALUE {
		// We have duplicate items here because the documentation
		// mentions one pair and the C API uses another.
		TDB_OPT_CONS_OUTPUT_FORMAT_DIR,
		TDB_OPT_CONS_OUTPUT_DIR,
		TDB_OPT_CONS_OUTPUT_FORMAT_PACKAGE,
		TDB_OPT_CONS_OUTPUT_PACKAGE,
	}

	public TrailDBConstructor(String root, String[] fields) throws FileNotFoundException {
		init(root, fields);
	}
	private native void init(String root, String[] fields);

	public void add(UUID uuid, int timestamp, String[] values) {
		byte uuidBytes[] = new byte[16];
		uuidBytes = uuidToBytes(uuid);
		native_add(uuidBytes, timestamp, values);
	}

	private native void native_add(byte[] uuid, int timestamp, String[] values);

	private static byte[] uuidToBytes(UUID uuid) {
		long hi = uuid.getMostSignificantBits();
		long lo = uuid.getLeastSignificantBits();
		return ByteBuffer.allocate(16).putLong(hi).putLong(lo).array();
	}

	public native void append(TrailDB tdb);
	public native void finalize();
	public native void close();
	public native void setOpt(TDB_OPT_CONS_KEY key, TDB_OPT_CONS_VALUE value);
	public native TDB_OPT_CONS_VALUE getOpt(TDB_OPT_CONS_KEY key);

	static {
		System.loadLibrary("TraildbJavaNative");
	}
}