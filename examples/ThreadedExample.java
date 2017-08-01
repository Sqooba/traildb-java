
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
		TrailDBCursor cursor = new TrailDBCursor(tdb);
		long n = tdb.numTrails();
		long totalSessions = 0;
		long totalEvents = 0;

		for (long i = 0; i < n; i++) {
			TrailDBEvent event;
			cursor.getTrail(i);
			event = cursor.next();
			long prevTime = event.timestamp;
			long numSessions = 1;
			long numEvents = 1;
			while ((event = cursor.next()) != null) {
				if (event.timestamp - prevTime > sessionLimit)
					numSessions++;
				prevTime = event.timestamp;
				numEvents++;
			}
			totalSessions += numSessions;
			totalEvents += numEvents;
		}
		cursor.free();

		System.out.println("Session Limit: " + sessionLimit + " Trails: " + n + " Sessions: " + totalSessions + " Events: " + totalEvents);
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
