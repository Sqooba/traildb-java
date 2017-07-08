
import traildb.TrailDBConstructor;
import traildb.TrailDB;

public class Example {
	public static void main(String[] args) {
		TrailDBConstructor cons = new TrailDBConstructor("tiny", new String[] {"user", "actn"});
		cons.finalize();

		System.out.println("Reached End");
	}
}