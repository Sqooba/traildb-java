
import traildb.TrailDB;
import traildb.TrailDBConstructor;
import traildb.TrailDBCursor;
import traildb.TrailDBEvent;
import traildb.TrailDBMultiCursor;
import traildb.TrailDBMultiEvent;

import java.util.UUID;
import java.io.FileNotFoundException;


class Event {
	UUID cookie;
	int timestamp;
	String[] values;
	Event(UUID cookie, int timestamp, String[] values) {
		this.cookie = cookie;
		this.timestamp = timestamp;
		this.values = values;
	}
}

public class MultiCursors {

	public static void read() throws FileNotFoundException {
		System.out.println("Reading");
		TrailDB tdb1 = new TrailDB("tiny1.tdb");
		TrailDB tdb2 = new TrailDB("tiny2.tdb");
		TrailDBCursor c1 = tdb1.cursorNew();
		TrailDBCursor c2 = tdb2.cursorNew();
		c1.getTrail(0);
		c2.getTrail(1);

		TrailDBCursor[] cursors = new TrailDBCursor[] {c1, c2};

		TrailDBMultiCursor multi = new TrailDBMultiCursor(cursors);
		TrailDBMultiEvent mevent;

		while ((mevent = multi.next()) != null) {
			System.out.println(mevent.event.timestamp);
		}
	}

	public static void write(String name, String[] fields, Event[] events) throws FileNotFoundException {
		TrailDBConstructor cons = new TrailDBConstructor(name, fields);
		for (int i = 0; i < events.length; i++) {
			cons.add(events[i].cookie, events[i].timestamp, events[i].values);
		}
		cons.finalize();
		cons.close();
		System.out.println("Wrote: " + name);
	}

	public static void main(String[] args) throws FileNotFoundException {
		UUID cookie1 = UUID.randomUUID();
		UUID cookie2 = UUID.randomUUID();

		write("tiny1", new String[] {"user", "action"}, new Event[] {
				new Event(cookie1, 2, new String[] {"bob", "run"}),
				new Event(cookie2, 4, new String[] {"fred", "walk"}),
				new Event(cookie1, 8, new String[] {"jerry", "speak"}),
				new Event(cookie2, 10, new String[] {"ted", "fly"}),
				new Event(cookie2, 12, new String[] {"doug", "dab"}),
		});

		write("tiny2", new String[] {"user", "action"}, new Event[] {
				new Event(cookie2, 1, new String[] {"cindy", "run"}),
				new Event(cookie2, 3, new String[] {"lucy", "walk"}),
				new Event(cookie1, 5, new String[] {"mary", "speak"}),
				new Event(cookie1, 6, new String[] {"sue", "fly"}),
				new Event(cookie2, 7, new String[] {"gunhilda of denmark", "dab"}),
		});

		System.out.println("Finished writing");
		read();
	}
}