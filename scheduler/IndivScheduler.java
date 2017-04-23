package scheduler;

import configure.ConfigHandler;
import factories.SchedulerFactory;
import util.DBProcessor;
import org.joda.time.DateTime;
import util.SQLProcessor;

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

    @Override
    public DateTime findEarliestAvailability(Student student, Assignment assignment){
        DateTime earliestTime = assignment.getCreatedAt().plusDays(1).minuteOfDay().setCopy(0);
        int[] dayPreference = student.getWorkDayPreference();
        int[] hourPreference = student.getWorkHourPreference();

        // Iterate until it finds the preferred day
        for (int i = 1; i < dayPreference.length; i++) {
            int dayOfWeek = earliestTime.getDayOfWeek();
            if (dayPreference[dayOfWeek] == 1)
                break;
            earliestTime = earliestTime.plusDays(1);
        }

        // Iterate until it finds the preferred time
        for (int i = 0; i < hourPreference.length; i++) {
            earliestTime = earliestTime.plusHours(1);
            int hourOfDay = earliestTime.getHourOfDay();
            if (hourPreference[hourOfDay] == 1)
                return earliestTime;
        }

        return null;
    }

    public boolean isOverlapped(ArrayList<Schedule> schedules,
                                DateTime startTime, DateTime endTime){
        for (int i = 0; i < schedules.size(); i++) {
            Schedule curSchedule = schedules.get(i);
        }
        return false;
    }

    @Override
    public boolean isEligible(Student student, Assignment assignment, DateTime availability){
        DateTime startTime = availability;
        DateTime endTime = availability.plusHours((int)Math.ceil(assignment.getHoursPerDay()));
        Schedule schedule = new Schedule(student.getId(), assignment.getId(), startTime, endTime,
                assignment.getPointsPossible() / assignment.getNumDays());

        // Check whether there is no overlapped schedule
        if(!isOverlapped(student.getSchedules(), startTime, endTime))
            student.addSchedule(schedule);

        return false;
    }

    // Generate a schedule table for each student.
    @Override
    public void generateScheduleTable(){
        for (int i = 0; i < students.length; i++) {
            Student curStudent = students[i];

            // Get assignment data related to the current student
            ArrayList<Assignment> curAssignments = curStudent.getAssignments();
            for (int j = 0; j < curAssignments.size(); j++) {
                Assignment curAssignment = curAssignments.get(j);
                // Find the earliest availability
                DateTime earliestAvail = findEarliestAvailability(curStudent, curAssignment);

                if (isEligible(curStudent, curAssignment, earliestAvail))
                    System.out.println();
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
