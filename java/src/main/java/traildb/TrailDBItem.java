package traildb;

public class TrailDBItem {
	public int field;
	public long value;
	public TrailDBItem(int field, long value) {
		this.field = field;
		this.value = value;
		init(field, value);
	}
	public native void init(int field, long value);
	public native int getField();
	public native long getValue();

	static {
		System.loadLibrary("TraildbJavaNative");
	}
}
