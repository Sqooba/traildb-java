import junit.framework.TestCase;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.Rule;

import traildb.*;

import java.util.UUID;
import java.io.FileNotFoundException;

public class TestTrailDB extends TestCase {

	protected void setUp() throws FileNotFoundException {
		System.out.println(System.getProperty("java.library.path"));
		TrailDBConstructor cons = new TrailDBConstructor("test", new String[] {"user", "action"});

		UUID cookie1 = UUID.randomUUID();
		UUID cookie2 = UUID.randomUUID();

		cons.add(cookie1, 1, new String[] {"bob", "run"});
		cons.add(cookie2, 2, new String[] {"fred", "walk"});
		cons.add(cookie1, 4, new String[] {"jerry", "speak"});
		cons.add(cookie1, 5, new String[] {"ted", "fly"});

		cons.finalize();
		cons.close();
	}

	public void testBasic() throws FileNotFoundException {
		TrailDB tdb = new TrailDB("test.tdb");
		TrailDBCursor cur = tdb.cursorNew();
		long numCookies = tdb.numTrails();
		TrailDBEvent event;
		for (int i=0; i < numCookies; i++) {
			cur.getTrail(i);
			while ((event = cur.next()) != null) {
				switch ((int) event.timestamp) {
					case 1:
						assertEquals(event.getItem(0), "bob");
						assertEquals(event.getItem(1), "run");
						break;
					case 2:
						assertEquals(event.getItem(0), "fred");
						assertEquals(event.getItem(1), "walk");
						break;
					case 4:
						assertEquals(event.getItem(0), "jerry");
						assertEquals(event.getItem(1), "speak");
						break;
					case 5:
						assertEquals(event.getItem(0), "ted");
						assertEquals(event.getItem(1), "fly");
						break;
					default:
						fail("Unrecognized timestamp " + event.timestamp);
				}
			}
		}
	}

	public void testExceptionFileNotFound() {
		try {
			TrailDB tdb = new TrailDB("wat.tdb");
			fail("Expected TrailDB to fail but it succeeded");
		} catch (FileNotFoundException e) {
			return;
		}
	}
}
