package io.sqooba.traildb;

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
public class TrailDBCursor implements Iterable<TrailDBEvent>, AutoCloseable {

    private ByteBuffer cursor;
    private TrailDBEvent event;
    // FixMe test parallel cursors
    // rename in tdbiterator

    protected TrailDBCursor(ByteBuffer cursor, TrailDBEvent event) {
        this.event = event;
        this.cursor = cursor;
    }

    @Override
    public void close() {
        if (this.cursor != null) {
            TrailDBNative.INSTANCE.cursorFree(this.cursor);
            this.cursor = null;
        }
    }

    @Override
    public Iterator<TrailDBEvent> iterator() {
        return new Iterator<TrailDBEvent>() {

            @Override
            public TrailDBEvent next() {
                if (!TrailDBCursor.this.event.isBuilt()) {
                    TrailDBNative.INSTANCE.cursorNext(TrailDBCursor.this.cursor, TrailDBCursor.this.event);
                }
                return TrailDBCursor.this.event;
            }

            @Override
            public boolean hasNext() {
                return TrailDBNative.INSTANCE.cursorNext(TrailDBCursor.this.cursor, TrailDBCursor.this.event) == 0;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}