package io.sqooba.traildbj;

import java.nio.ByteBuffer;
import java.util.Iterator;

/**
 * Class representing a cursor over a particular trail of the database. The cursor is initially constructed from the
 * TrailDB.trail() method. The cursor points to the current event and this event is updated each time a .next() is
 * called.
 * 
 * @author B. Sottas
 *
 */
public class TrailDBCursor implements Iterable<TrailDBEvent> {

    private ByteBuffer cursor;
    private TrailDBEvent event;

    protected TrailDBCursor(ByteBuffer cursor, TrailDBEvent event) {
        this.event = event;
        this.cursor = cursor;
    }

    @Override
    protected void finalize() {
        if (this.cursor != null) {
            TrailDBj.INSTANCE.tdbCursorFree(this.cursor);
        }
    }

    @Override
    public Iterator<TrailDBEvent> iterator() {
        return new Iterator<TrailDBEvent>() {

            int errCode = 0;

            @Override
            public TrailDBEvent next() {
                if (!TrailDBCursor.this.event.isBuilt()) {
                    TrailDBj.INSTANCE.tdbCursorNext(TrailDBCursor.this.cursor, TrailDBCursor.this.event);
                }
                return TrailDBCursor.this.event;
            }

            @Override
            public boolean hasNext() {
                return TrailDBj.INSTANCE.tdbCursorNext(TrailDBCursor.this.cursor, TrailDBCursor.this.event) == 0;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
