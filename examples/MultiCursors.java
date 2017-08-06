
import traildb.TrailDB;
import traildb.TrailDBConstructor;
import traildb.TrailDBTrail;
import traildb.TrailDBMultiTrail;

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

	public static void read(UUID[] cookies) throws FileNotFoundException {
		System.out.println("Reading");
		TrailDB tdb1 = new TrailDB("tiny1.tdb");
		TrailDB tdb2 = new TrailDB("tiny2.tdb");
		TrailDBTrail trail1 = new TrailDBTrail(tdb1, 0);
		TrailDBTrail trail2 = new TrailDBTrail(tdb2, 1);

		TrailDBTrail[] trails = new TrailDBTrail[] {trail1, trail2};

		TrailDBMultiTrail multi = new TrailDBMultiTrail(trails);

		while (multi.next() != null) {
			System.out.println(multi.getTimestamp());
//			System.out.println(multi.getItem(0));
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
		read(new UUID[] {cookie1, cookie2});
	}
}