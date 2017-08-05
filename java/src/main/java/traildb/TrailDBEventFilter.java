package traildb;

public class TrailDBEventFilter {
	public TrailDBEventFilter() {
		init();
	}

	private native void init();

	public static native TrailDBEventFilter matchNone();

	public static native TrailDBEventFilter matchAll();

	public native void free();

	// public native void addTerm(TrailDBItem item, boolean negative); TODO: Fix this

	public native void addTimeRange(int startTime, int endTime);

	public native void newClause();

	public native int numClauses();

	public native int numTerms(int clauseIndex);

	// public native void getTermType(int clauseIndex, termIndex, ???) TODO: Fix this

	// TODO: Cache these results at the Java layer
	public native boolean isNegative(int clauseIndex, int termIndex);

	public native int getStartTime(int clauseIndex, int termIndex);

	public native int getEndTime(int clauseIndex, int termIndex);

	static {
		System.loadLibrary("TraildbJavaNative");
	}
}