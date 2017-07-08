import java.util.UUID;

public class TrailDBConstructor {
	public native void add(UUID uuid, int timestamp, String[] values);
	public native void finalize();
	public native void close();
}