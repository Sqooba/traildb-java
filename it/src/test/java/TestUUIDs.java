import junit.framework.TestCase;

import traildb.*;

import java.util.UUID;
import java.io.FileNotFoundException;

public class TestUUIDs extends TestCase {
	UUID cookie1;
	UUID cookie2;
	protected void setUp() throws FileNotFoundException {
		TrailDBConstructor cons = new TrailDBConstructor("test", new String[] {"user", "action"});

		cookie1 = UUID.randomUUID();
		cookie2 = UUID.randomUUID();

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
		long numCookies = tdb.numTrails();

		for (long i=0; i < numCookies; i++) {
			UUID uuid = tdb.getUUID(i);

			long tid = tdb.getTrailId(uuid);
			assertEquals(i, tid);

			assertTrue(uuid.equals(cookie1) || uuid.equals(cookie2));
		}
	}
}