package traildb.filters;


class TrailDBRestriction implements TrailDBTerm {
	String field;
	String value;
	boolean negative;
	TrailDBRestriction(String field, String value, boolean negative) {
		this.field = field;
		this.value = value;
		this.negative = negative;
	}
}
