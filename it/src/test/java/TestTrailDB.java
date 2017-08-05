import junit.framework.TestCase;

import traildb.*;

import java.util.UUID;
import java.io.FileNotFoundException;

public class TestTrailDB extends TestCase {

	protected void setUp() throws FileNotFoundException {
		TrailDBConstructor cons = new TrailDBConstructor("test", new String[] {"user", "action"});

		UUID cookie1 = UUID.randomUUID();
		UUID cookie2 = UUID.randomUUID();

		cons.add(cookie1, 1, new String[] {"bob", "run"});
		cons.add(cookie2, 2, new String[] {"fred", "walk"});
		cons.add(cookie1, 4, new String[] {"jerry", "speak"});
		cons.add(cookie1, 5, new String[] {"ted", "fly"});
		cons.add(cookie2, 6, new String[] {"doug", "dab"});

		cons.finalize();
		cons.close();
	}

	public void testBasic() throws FileNotFoundException {
		TrailDB tdb = new TrailDB("test.tdb");
		TrailDBTrail trail = new TrailDBTrail(tdb, 0);
		long numCookies = tdb.numTrails();
		for (int i=0; i < numCookies; i++) {
			trail.getTrail(i);
			while (trail.next() != null) {
				switch ((int) trail.getTimestamp()) {
					case 1:
						assertEquals(trail.getItem(0), "bob");
						assertEquals(trail.getItem(1), "run");
						break;
					case 2:
						assertEquals(trail.getItem(0), "fred");
						assertEquals(trail.getItem(1), "walk");
						break;
					case 4:
						assertEquals(trail.getItem(0), "jerry");
						assertEquals(trail.getItem(1), "speak");
						break;
					case 5:
						assertEquals(trail.getItem(0), "ted");
						assertEquals(trail.getItem(1), "fly");
						break;
					case 6:
						assertEquals(trail.getItem(0), "doug");
						assertEquals(trail.getItem(1), "dab");
						break;
					default:
						fail("Unrecognized timestamp " + trail.getTimestamp());
				}
			}
		}
	}

	public void testPeek() throws FileNotFoundException {
		TrailDB tdb = new TrailDB("test.tdb");
		TrailDBTrail trail = new TrailDBTrail(tdb, 0);
		long numCookies = tdb.numTrails();
		int foundEvents = 0;
		String[] peekValues = new String[] {"", ""};

		for (int i=0; i < numCookies; i++) {
			trail.getTrail(i);
			while (trail.next() != null) {
				if (peekValues[0] != "") {
					assertEquals(trail.getItem(0), peekValues[0]);
					assertEquals(trail.getItem(1), peekValues[1]);
				}
				foundEvents++;

				switch ((int) trail.getTimestamp()) {
					case 1:
						assertEquals(trail.getItem(0), "bob");
						assertEquals(trail.getItem(1), "run");
						break;
					case 2:
						assertEquals(trail.getItem(0), "fred");
						assertEquals(trail.getItem(1), "walk");
						break;
					case 4:
						assertEquals(trail.getItem(0), "jerry");
						assertEquals(trail.getItem(1), "speak");
						break;
					case 5:
						assertEquals(trail.getItem(0), "ted");
						assertEquals(trail.getItem(1), "fly");
						break;
					case 6:
						assertEquals(trail.getItem(0), "doug");
						assertEquals(trail.getItem(1), "dab");
						break;
					default:
						fail("Unrecognized timestamp " + trail.getTimestamp());
				}

				if (trail.peek() != null) {
					peekValues = new String[] { trail.getItem(0), trail.getItem(1) };
				} else {
					peekValues = new String[] {"", ""};
				}
			}
		}
		assertEquals(foundEvents, 5);
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
