package traildb.filters;


public class TrailDBTimeRange implements TrailDBTerm {
	long start;
	long end;
	public TrailDBTimeRange(long start, long end) {
		this.start = start;
		this.end = end;
	}
}
