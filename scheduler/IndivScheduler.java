package scheduler;

import configure.ConfigHandler;
import factories.SchedulerFactory;
import util.DBProcessor;
import org.joda.time.DateTime;
import util.SQLProcessor;
import util.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by powerswat on 3/8/17.
 */
public class IndivScheduler implements SchedulerFactory{
    private static IndivScheduler instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;

    private static String tableName = "SCHEDULES";
    private static String pkCol = "ID";
    private static Student[] students;

    public static IndivScheduler getInstance(ConfigHandler cnfgHndlr, DBProcessor dbProcessor){
        if (instance == null){
            synchronized (IndivScheduler.class){
                if (instance == null)
                    instance = new IndivScheduler(cnfgHndlr, dbProcessor);
            }
        }
        return instance;
    }

    public IndivScheduler(ConfigHandler cnfgHndlr, DBProcessor dbProcessor){
        this.cnfgHndlr = cnfgHndlr;
        this.dbProcessor = dbProcessor;
    }

    @Override
    public void runScheduler(ArrayList<ArrayList<String>> sqlData, Student[] students){
        this.students = students;

        // Plan based on constraints
        planSchedule(sqlData);

        System.out.println();
    }

    // Generate a schedule table for each student.
    @Override
    public void generateScheduleTable(){
        TimeUtil timeUtil = new TimeUtil();
        for (int i = 0; i < students.length; i++) {
            Student curStudent = students[i];

            // Get assignment data related to the current student
            ArrayList<Assignment> curAssignments = curStudent.getAssignments();
            for (int j = 0; j < curAssignments.size(); j++) {
                // TODO: Generate all schedules for each assignment here.

                Assignment curAssignment = curAssignments.get(j);

                // Find the earliest availability
                DateTime earliestAvail = timeUtil.findEarliestAvailability(curStudent, curAssignment);

                // Generate an available schedule based on the user's preference
                Schedule schedule = timeUtil.generateSchedule(curStudent, curAssignment, earliestAvail);

                // Check the eligibility of the given schedule
                if (timeUtil.isEligible(curStudent, schedule))
                    curStudent.addSchedule(schedule);
            }
            System.out.println();
        }
    }

    @Override
    public void planSchedule(ArrayList<ArrayList<String>> sqlData) {
        // Generate a schedule table for each student.
        generateScheduleTable();

        System.out.println();
    }

    @Override
    public void insertIntoTable() {

    }
}
