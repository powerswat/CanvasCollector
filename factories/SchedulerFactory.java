package factories;

import org.joda.time.DateTime;
import scheduler.Assignment;
import scheduler.Student;

import java.util.ArrayList;

/**
 * Created by powerswat on 3/17/17.
 */
public interface SchedulerFactory {
    void runScheduler(ArrayList<ArrayList<String>> sqlData, Student[] students);
    void generateScheduleTable();
    void insertIntoTable();
}
