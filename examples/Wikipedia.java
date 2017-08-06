
import traildb.*;

import java.io.FileNotFoundException;

public class Wikipedia {
	public static long SESSION_LIMIT = 30 * 60;

	public static void sessions(TrailDB tdb) {
		System.out.println("Number of events: " + tdb.numEvents());
		TrailDBTrail trail = new TrailDBTrail(tdb, 0);
		long numTrails = tdb.numTrails();
		long totalSessions = 0;
		long totalEvents = 0;

		do {
			trail.next();
			long prevTime = trail.getTimestamp();
			long numSessions = 1;
			long numEvents = 1;
			while (trail.next() != null) {
				if (trail.getTimestamp() - prevTime > SESSION_LIMIT)
					numSessions++;
				prevTime = trail.getTimestamp();
				numEvents++;
			}
			totalSessions += numSessions;
			totalEvents += numEvents;
		} while (trail.nextTrail());

		for (long i = 0; i < numTrails; i++) {

		}
		System.out.println("Trails: " + numTrails + " Sessions: " + totalSessions + " Events: " + totalEvents);
	}

	public static void main(String[] args) throws FileNotFoundException {
		TrailDB tdb = new TrailDB("wikipedia-history-small.tdb");
		sessions(tdb);
	}
}