package io.sqooba.traildb;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p> Class representing a cursor over a particular trail of the database. The cursor is initially constructed from the
 * TrailDB.trail() method. The cursor points to the current event and this event is updated each time a .next() is
 * called.
 *
 * <p> The TrailDBIterator should be used in a try-with-resource block so it gets closed and can free the memory after
 * being done iterating over a trail.
 *
 * @author B. Sottas
 *
 */
public class TrailDBIterator implements Iterable<TrailDBEvent>, AutoCloseable {

    private ByteBuffer cursor;
    private final TrailDB trailDB;
    private final long size;

    protected TrailDBIterator(ByteBuffer cursor, TrailDB trailDB, int size) {
        this.cursor = cursor;
        this.trailDB = trailDB;
        this.size = size;
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

            private TrailDBEvent event = new TrailDBEvent();
            int currIndex = 0;

            @Override
            public TrailDBEvent next() {
                this.event = new TrailDBEvent(TrailDBIterator.this.trailDB, TrailDBIterator.this.trailDB.fields);

                if (TrailDBNative.INSTANCE.cursorNext(TrailDBIterator.this.cursor, this.event) == -1) {
                    throw new NoSuchElementException();
                }

                this.currIndex++;
                return this.event;
            }

            @Override
            public boolean hasNext() {
                return this.currIndex < TrailDBIterator.this.size;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
