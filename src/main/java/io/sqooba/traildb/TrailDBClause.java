package io.sqooba.traildb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrailDBClause {

    private TrailDBTerm terms[];
    private TrailDBTimeRange timeRanges[];

    private TrailDBClause(List<TrailDBTerm> terms, List<TrailDBTimeRange> timeRanges) {
        this.terms = terms.toArray(new TrailDBTerm[0]);
        this.timeRanges = timeRanges.toArray(new TrailDBTimeRange[0]);
    }

    public TrailDBTerm[] getTerms() {
        return this.terms;
    }

    public TrailDBTimeRange[] getTimeRanges() {
        return this.timeRanges;
    }

    public static class TrailDBClauseBuilder {

        private List<TrailDBTerm> terms = new ArrayList<>();
        private List<TrailDBTimeRange> timeRanges = new ArrayList<>();

        public TrailDBClauseBuilder addTerm(TrailDBTerm term) {
            this.terms.add(term);

            return this;
        }

        public TrailDBClauseBuilder addTimeRange(TrailDBTimeRange timeRange) {
            this.timeRanges.add(timeRange);

            return this;
        }

        public TrailDBClause build() {
            return new TrailDBClause(this.terms, this.timeRanges);
        }

    }

}
