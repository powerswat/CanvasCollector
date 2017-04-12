package factories;

import scheduler.Student;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by powerswat on 3/17/17.
 */
public interface SchedulerFactory {
    public void runScheduler(ArrayList<String[]> sqlData, Student[] students);
    public void planSchedule(ArrayList<String[]> sqlData);
    public void insertIntoTable();
}
