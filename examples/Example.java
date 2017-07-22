
import traildb.TrailDB;
import traildb.TrailDBConstructor;
import traildb.TrailDBCursor;
import traildb.TrailDBEvent;

import java.util.UUID;
import java.io.FileNotFoundException;

public class Example {

	public static void read() throws FileNotFoundException {
		System.out.println("Begin Test");
		TrailDB tdb = new TrailDB("tiny.tdb");
		TrailDBCursor cur = tdb.cursorNew();
		TrailDBEvent e1;
		TrailDBEvent e2;

		cur.getTrail(0);

		e1 = cur.next();

		cur.getTrail(1);

		e2 = cur.next();
		System.out.println("End Test");
	}

	public static void main(String[] args) throws FileNotFoundException {
		TrailDBConstructor cons = new TrailDBConstructor("tiny", new String[] {"user", "action"});

		UUID cookie1 = UUID.randomUUID();
		UUID cookie2 = UUID.randomUUID();

		cons.add(cookie1, 1, new String[] {"bob", "run"});
		cons.add(cookie2, 2, new String[] {"fred", "walk"});
		cons.add(cookie1, 4, new String[] {"jerry", "speak"});
		cons.add(cookie1, 5, new String[] {"ted", "fly"});

		cons.finalize();
		cons.close();

		System.out.println("Finished writing");
		read();
	}
}