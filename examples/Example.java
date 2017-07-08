
import traildb.TrailDBConstructor;
import traildb.TrailDB;

import java.util.UUID;

public class Example {
	public static void main(String[] args) {
		TrailDBConstructor cons = new TrailDBConstructor("tiny", new String[] {"user", "action"});

		UUID cookie1 = UUID.randomUUID();
		UUID cookie2 = UUID.randomUUID();

		cons.add(cookie1, 1, new String[] {"bob", "run"});
		cons.add(cookie2, 2, new String[] {"fred", "walk"});
		cons.add(cookie1, 4, new String[] {"jerry", "speak"});

		cons.finalize();
		cons.close();

		System.out.println("Reached End");
	}
}