package io.sqooba.traildb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrailDBClause {

    private List<String[]> terms;
    private List<Long[]> timeRanges;

    public TrailDBClause(List<String[]> terms, List<Long[]> timeRanges) {
        if (terms == null) {
            this.terms = new ArrayList<>();
        } else {
            this.terms = terms;
        }
        if (timeRanges == null) {
            this.timeRanges = new ArrayList<>();
        } else {
            this.timeRanges = timeRanges;
        }
    }

    public TrailDBClause() {
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
        return this.timeRanges;
    }

}
