
import traildb.*;
import traildb.filters.*;

import java.util.UUID;
import java.io.FileNotFoundException;

class Event {
  UUID cookie;
  int timestamp;
  String[] values;
  Event(UUID cookie, int timestamp, String[] values) {
    this.cookie = cookie;
    this.timestamp = timestamp;
    this.values = values;
  }
}

public class EventFilter {

  public static void test1(UUID[] cookies) throws FileNotFoundException {
    TrailDB tdb1 = new TrailDB("tiny1.tdb");
    TrailDBTrail trail = new TrailDBTrail(tdb1, 0);
    /*
     (device!=mobile OR action=pageview OR 500 <= t < 1000) AND (device=mobile OR action=purchase OR 400 <= t < 1100)

     t=310, device=mobile, action=pageview *
     t=320, device=desktop, action=pageview
     t=330, device=mobile, action=purchase
     t=340, device=desktop, action=purchase *

     t=410, device=mobile, action=pageview *
     t=420, device=desktop, action=pageview *
     t=430, device=mobile, action=purchase
     t=440, device=desktop, action=purchase *

     t=510, device=mobile, action=pageview *
     t=520, device=desktop, action=pageview *
     t=530, device=mobile, action=purchase *
     t=540, device=desktop, action=purchase *

     t=1010, device=mobile, action=pageview *
     t=1020, device=desktop, action=pageview *
     t=1030, device=mobile, action=purchase
     t=1040, device=desktop, action=purchase *

     t=1110, device=mobile, action=pageview *
     t=1120, device=desktop, action=pageview
     t=1130, device=mobile, action=purchase
     t=1140, device=desktop, action=purchase *

     */
    TrailDBEventFilter filter = new TrailDBEventFilter(tdb1,
        new TrailDBClause(
            new TrailDBRestriction("device", "mobile", true),
            new TrailDBRestriction("action", "pageview"),
            new TrailDBTimeRange(500, 1000)
        ),
        new TrailDBClause(
            new TrailDBRestriction("device", "mobile"),
            new TrailDBRestriction("action", "purchase"),
            new TrailDBTimeRange(400, 1100)
        )
    );

    trail.setEventFilter(filter);

    while (trail.next() != null) {
      System.out.println(trail.getTimestamp());
    }
  }

  public static void test0(UUID[] cookies) throws FileNotFoundException {
    TrailDB tdb1 = new TrailDB("tiny1.tdb");
    TrailDBTrail trail = new TrailDBTrail(tdb1, 0);
    /*
     (500 <= t < 1000)

     t=300, device=mobile, action=pageview
     t=310, device=desktop, action=pageview
     t=320, device=mobile, action=purchase
     t=330, device=desktop, action=purchase

     t=410, device=mobile, action=pageview
     t=420, device=desktop, action=pageview
     t=430, device=mobile, action=purchase
     t=440, device=desktop, action=purchase

     t=510, device=mobile, action=pageview *
     t=520, device=desktop, action=pageview *
     t=530, device=mobile, action=purchase *
     t=540, device=desktop, action=purchase *

     t=1010, device=mobile, action=pageview
     t=1020, device=desktop, action=pageview
     t=1030, device=mobile, action=purchase
     t=1040, device=desktop, action=purchase

     t=1110, device=mobile, action=pageview
     t=1120, device=desktop, action=pageview
     t=1130, device=mobile, action=purchase
     t=1140, device=desktop, action=purchase

     */
    TrailDBEventFilter filter = new TrailDBEventFilter(tdb1,
      new TrailDBClause(
        new TrailDBTimeRange(500, 1000)
      )
    );

    trail.setEventFilter(filter);

    while (trail.next() != null) {
      System.out.println(trail.getTimestamp());
    }
  }

  public static void write(String name, String[] fields, Event[] events) throws FileNotFoundException {
    TrailDBConstructor cons = new TrailDBConstructor(name, fields);
    for (int i = 0; i < events.length; i++) {
      cons.add(events[i].cookie, events[i].timestamp, events[i].values);
    }
    cons.finalize();
    cons.close();
    System.out.println("Wrote: " + name);
  }

  public static void main(String[] args) throws FileNotFoundException {
    UUID cookie1 = UUID.randomUUID();
    UUID cookie2 = UUID.randomUUID();

    write("tiny1", new String[] {"device", "action"}, new Event[] {
      new Event(cookie1, 310, new String[] {"mobile", "pageview"}),
      new Event(cookie1, 320, new String[] {"desktop", "pageview"}),
      new Event(cookie1, 330, new String[] {"mobile", "purchase"}),
      new Event(cookie1, 340, new String[] {"desktop", "purchase"}),

      new Event(cookie1, 410, new String[] {"mobile", "pageview"}),
      new Event(cookie1, 420, new String[] {"desktop", "pageview"}),
      new Event(cookie1, 430, new String[] {"mobile", "purchase"}),
      new Event(cookie1, 440, new String[] {"desktop", "purchase"}),

      new Event(cookie1, 510, new String[] {"mobile", "pageview"}),
      new Event(cookie1, 520, new String[] {"desktop", "pageview"}),
      new Event(cookie1, 530, new String[] {"mobile", "purchase"}),
      new Event(cookie1, 540, new String[] {"desktop", "purchase"}),

      new Event(cookie1, 1010, new String[] {"mobile", "pageview"}),
      new Event(cookie1, 1020, new String[] {"desktop", "pageview"}),
      new Event(cookie1, 1030, new String[] {"mobile", "purchase"}),
      new Event(cookie1, 1040, new String[] {"desktop", "purchase"}),

      new Event(cookie1, 1110, new String[] {"mobile", "pageview"}),
      new Event(cookie1, 1120, new String[] {"desktop", "pageview"}),
      new Event(cookie1, 1130, new String[] {"mobile", "purchase"}),
      new Event(cookie1, 1140, new String[] {"desktop", "purchase"}),
    });

    System.out.println("Finished writing");
    test1(new UUID[] {cookie1, cookie2});
  }
}