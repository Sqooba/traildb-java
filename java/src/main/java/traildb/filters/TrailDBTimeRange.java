package traildb.filters;


class TrailDBTimeRange implements TrailDBTerm {
	long start;
	long end;
	TrailDBTimeRange(long start, long end) {
		this.start = start;
		this.end = end;
	}
}
