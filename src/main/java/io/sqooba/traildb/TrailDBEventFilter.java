package io.sqooba.traildb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TrailDBEventFilter {

    private TrailDBNative trailDB = TrailDBNative.INSTANCE;

    private final List<String[]> clauses;
    private final TrailDB db;
    ByteBuffer filter;

    private TrailDBEventFilter(TrailDBEventFilterBuilder builder, TrailDB db) {
        this.clauses = builder.clauses;
        this.db = db;
        init();
    }

    private void init() {
        this.filter = this.trailDB.eventFilterNew();
        if (this.filter == null) {
            throw new TrailDBException("Failed to allocate memory for new filter.");
        }

        for(int i = 0; i < this.clauses.size(); i++) {
            if (i > 0) {
                int errCode = this.trailDB.eventFilterNewClause(this.filter);
                if (errCode != 0) {
                    throw new TrailDBException("Failed to add new clause to filter");
                }
            }

            String[] currentClause = this.clauses.get(i);
            int len = currentClause.length;
            String field = "";
            String value = "";
            int isNegative = 0;
            for(int j = 0; j < len; j++) { // Done 2 or 3 times.
                field = currentClause[0];
                value = currentClause[1];
                if (len == 3) { // This means there is a negation maybe.
                    isNegative = "true".equals(currentClause[2]) ? 1 : 0;
                }

                long item = this.db.getItem(this.db.getField(field), value); // TODO put try catch

                int errCode = this.trailDB.eventFilterAddTerm(this.filter, item, isNegative);
                if (errCode != 0) {
                    throw new TrailDBException("Failed to add term.");
                }
            }

        }
    }

    public void destroy() {
        if (this.filter != null) {
            this.trailDB.eventFilterFree(this.filter);
        }
    }

    public static class TrailDBEventFilterBuilder {

        private TrailDB db;

        private List<String[]> clauses;

        public TrailDBEventFilterBuilder(TrailDB db) {
            this.db = db;
            this.clauses = new ArrayList<>();
        }

        public TrailDBEventFilterBuilder addClause(String[] clause) {
            this.clauses.add(clause);
            return this;
        }

        public TrailDBEventFilter build() {
            return new TrailDBEventFilter(this, this.db);
        }
    }

}
