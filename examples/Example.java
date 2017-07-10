
import traildb.TrailDB;
import traildb.TrailDBConstructor;
import traildb.TrailDBCursor;
import traildb.TrailDBEvent;

import java.util.UUID;

public class Example {

	public static void read() {
		TrailDB tdb = new TrailDB("tiny.tdb");
		TrailDBCursor cur = tdb.cursorNew();
		int numCookies = tdb.numTrails();
		UUID uuid;
		TrailDBEvent event;
		for (int i=0; i < numCookies; i++) {
			uuid = tdb.getUUID(i);
			cur.getTrail(i);
			event = cur.next();
			System.out.println(uuid);
			System.out.println(event.timestamp);
		}
	}

	public static void main(String[] args) {
		TrailDBConstructor cons = new TrailDBConstructor("tiny", new String[] {"user", "action"});

		UUID cookie1 = UUID.randomUUID();
		UUID cookie2 = UUID.randomUUID();

		System.out.println(cookie1 + " " + cookie2);

		cons.add(cookie1, 1, new String[] {"bob", "run"});
		cons.add(cookie2, 2, new String[] {"fred", "walk"});
		cons.add(cookie1, 4, new String[] {"jerry", "speak"});

		cons.finalize();
		cons.close();

		read();
	}
}