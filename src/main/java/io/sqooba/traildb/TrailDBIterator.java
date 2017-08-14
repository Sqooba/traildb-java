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
public class TrailDBIterator implements Iterable<TrailDBEvent>, AutoCloseable {

    private ByteBuffer cursor;
    private boolean built = false;
    private TrailDB trailDB;
    private long size;

    protected TrailDBIterator(ByteBuffer cursor, TrailDB trailDB) {
        this.cursor = cursor;
        this.trailDB = trailDB;
        this.size = trailDB.getNumEvents();
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

            private TrailDBEvent event;

            @Override
            public TrailDBEvent next() {
                if (!TrailDBIterator.this.built) {
                    this.event = TrailDBNative.INSTANCE.cursorNext(TrailDBIterator.this.cursor);
                    TrailDBIterator.this.built = true;
                }
                this.event.build(TrailDBIterator.this.trailDB, TrailDBIterator.this.trailDB.fields);
                return this.event;
            }

            @Override
            public boolean hasNext() {
                TrailDBEvent next = TrailDBNative.INSTANCE.cursorNext(TrailDBIterator.this.cursor);
                if (next == null) {
                    return false;
                } else {
                    this.event = next;
                    TrailDBIterator.this.built = true;
                    return true;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
