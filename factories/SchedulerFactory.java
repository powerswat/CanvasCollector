package factories;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by powerswat on 3/17/17.
 */
public interface SchedulerFactory {
    public void runScheduler(ArrayList<String[]> sqlData);
    public void planSchedule(ArrayList<String[]> sqlData);
    public void insertTable();
}
