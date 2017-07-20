package io.sqooba.traildb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrailDBFilterClause {

    private List<String[]> terms;
    private List<Long[]> timeRanges;

    public TrailDBFilterClause(List<String[]> terms, List<Long[]> timeRanges) {
        this.terms = terms;
        this.timeRanges = timeRanges;
    }

    public TrailDBFilterClause() {
        this.terms = new ArrayList<>();
        this.timeRanges = new ArrayList<>();
    }

    public void addTerm(String[] term) {
        this.terms.add(term);
    }

    public void addTimeRange(Long[] timeRange) {
        this.timeRanges.add(timeRange);
    }

    public List<String[]> getTerms() {
        return Collections.unmodifiableList(this.terms);
    }

    public List<Long[]> getTimeRanges() {
        return Collections.unmodifiableList(this.timeRanges);
    }

}
