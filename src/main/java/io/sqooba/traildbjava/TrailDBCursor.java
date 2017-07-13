package io.sqooba.traildbjava;

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
    // FixMe test parallel cursors
    // rename in tdbiterator

    protected TrailDBCursor(ByteBuffer cursor, TrailDBEvent event) {
        this.event = event;
        this.cursor = cursor;
    }

    // FixMe
    @Override
    protected void finalize() {
        if (this.cursor != null) {
            TrailDBj.INSTANCE.cursorFree(this.cursor);
        }
    }

    @Override
    public Iterator<TrailDBEvent> iterator() {
        return new Iterator<TrailDBEvent>() {

            @Override
            public TrailDBEvent next() {
                if (!TrailDBCursor.this.event.isBuilt()) {
                    TrailDBj.INSTANCE.cursorNext(TrailDBCursor.this.cursor, TrailDBCursor.this.event);
                }
                return TrailDBCursor.this.event;
            }

            @Override
            public boolean hasNext() {
                return TrailDBj.INSTANCE.cursorNext(TrailDBCursor.this.cursor, TrailDBCursor.this.event) == 0;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
