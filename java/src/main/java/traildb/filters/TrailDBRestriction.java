package traildb.filters;


public class TrailDBRestriction implements TrailDBTerm {
	String field;
	String value;
	boolean negative;
	public TrailDBRestriction(String field, String value, boolean negative) {
		this.field = field;
		this.value = value;
		this.negative = negative;
	}
	public TrailDBRestriction(String field, String value) {
		this(field, value, false);
	}
}
