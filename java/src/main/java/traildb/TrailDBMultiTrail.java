package traildb;

public class TrailDBMultiTrail {
	public TrailDBTrail event;
	public long cursorIndex;

	private long cur;

	public TrailDBMultiTrail(TrailDBTrail[] cursors) {
		init(cursors);
	}

	private native void init(TrailDBTrail[] cursors);

	public native void free();

	public native void reset();

	public native TrailDBMultiTrail next();

	public native TrailDBMultiTrail[] nextBatch(int maxEvents);

	public native TrailDBMultiTrail peek();

	private static native void initIDs();

	static {
		System.loadLibrary("TraildbJavaNative");
		initIDs();
	}
}
