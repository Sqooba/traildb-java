
import traildb.*;

import java.io.FileNotFoundException;

class Parallel implements Runnable {
	TrailDB tdb;
	int sessionLimit;
	Parallel(TrailDB tdb, int sessionLimit) {
		this.tdb = tdb;
		this.sessionLimit = sessionLimit;
	}
	public void run() {
		System.out.println("Session Limit: " + sessionLimit);
		TrailDBTrail trail = new TrailDBTrail(tdb, 0);
		long numTrails = tdb.numTrails();
		long totalSessions = 0;
		long totalEvents = 0;

		for (long i = 0; i < numTrails; i++) {
			trail.getTrail(i);
			trail.peek();
			long prevTime = trail.getTimestamp();
			long numSessions = 1;
			long numEvents = 1;
			while (trail.next() != null) {
				if (trail.getTimestamp() - prevTime > sessionLimit)
					numSessions++;
				prevTime = trail.getTimestamp();
				numEvents++;
			}
			totalSessions += numSessions;
			totalEvents += numEvents;
		}

		System.out.println("Session Limit: " + sessionLimit + " Trails: " + numTrails + " Sessions: " + totalSessions + " Events: " + totalEvents);
	}
}

class ThreadedExample {

	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		TrailDB tdb = new TrailDB("wikipedia-history-small.tdb");
		int n = 8;
		Thread[] threads = new Thread[n];

		for (int i = 0; i < n; i++) {
			Thread thread = new Thread(new Parallel(tdb, 60 * (i + 1)), "" + i);
			threads[i] = thread;
			thread.start();
		}
		for (int i = 0; i < n; i++) {
			threads[i].join();
		}

		System.out.println("Closing tdb");
		tdb.close();
	}
}
