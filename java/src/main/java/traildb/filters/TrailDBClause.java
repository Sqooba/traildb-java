package traildb.filters;

public class TrailDBClause {
	TrailDBTerm[] terms;
	public TrailDBClause(TrailDBTerm... terms) {
		this.terms = terms;
	}
}
