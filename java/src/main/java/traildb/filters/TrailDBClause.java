package traildb.filters;

public class TrailDBClause {
	TrailDBTerm[] terms;
	TrailDBClause(TrailDBTerm... terms) {
		this.terms = terms;
	}
}
