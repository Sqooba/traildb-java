package io.sqooba.traildb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TrailDBEventFilter {

    private TrailDBNative trailDB = TrailDBNative.INSTANCE;

    private final TrailDBClause clauses[];
    private final TrailDB db;
    ByteBuffer filter;

    public TrailDBEventFilter(TrailDB db, TrailDBClause... clauses) {
        this.clauses = clauses;
        this.db = db;
        init();
    }

    private void init() {
        this.filter = this.trailDB.eventFilterNew();
        if (this.filter == null) {
            throw new TrailDBException("Failed to allocate memory for new filter.");
        }

        for(int i = 0; i < this.clauses.length; i++) {
            if (i > 0) {
                int errCode = this.trailDB.eventFilterNewClause(this.filter);
                if (errCode != 0) {
                    throw new TrailDBException("Failed to add new clause to filter");
                }
            }

            TrailDBClause currentClause = this.clauses[i];

            // Add terms filtering.
            for(TrailDBTerm term : currentClause.getTerms()) {

                long item = this.db.getItem(this.db.getField(term.getFieldName()), term.getFieldValue()); // TODO put try catch

                int errCode = this.trailDB.eventFilterAddTerm(this.filter, item, term.isNegative() ?  1: 0);
                if (errCode != 0) {
                    throw new TrailDBException("Failed to add term.");
                }
            }

            // Add time range filtering.
            for(TrailDBTimeRange timeRange : currentClause.getTimeRanges()) {
                int errCode = this.trailDB.eventFilterAddTimeRange(this.filter, timeRange.getBeginTimestamp(),
                        timeRange.getEndTimestamp());
                if (errCode != 0) {
                    throw new TrailDBException("Failed to add time range.");
                }
            }

        }
    }

    public void destroy() {
        if (this.filter != null) {
            this.trailDB.eventFilterFree(this.filter);
        }
    }

}
