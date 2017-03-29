package factories;

import org.joda.time.DateTime;

/**
 * Created by powerswat on 3/29/17.
 */
public interface TimeOpFactory {
    public DateTime addTime(DateTime t1, DateTime t2);
}
