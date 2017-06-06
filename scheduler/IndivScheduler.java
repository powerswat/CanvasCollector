package scheduler;

import configure.ConfigHandler;
import factories.SchedulerFactory;
import org.joda.time.format.DateTimeFormat;
import util.DBProcessor;
import util.TimeUtil;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by powerswat on 3/8/17.
 */
public class IndivScheduler implements SchedulerFactory{
    private static IndivScheduler instance = null;

    private static DBProcessor dbProcessor;

    private static String tableName = "SCHEDULES";
    private static String pkCol = "ID";
    private static String[] colNames;
    private static String[] types;

    private static Student[] students;

    public static IndivScheduler getInstance(ConfigHandler cnfgHndlr, DBProcessor dbProcessor,
                                             String[] colNames, String[] types){
        if (instance == null){
            synchronized (IndivScheduler.class){
                if (instance == null)
                    instance = new IndivScheduler(cnfgHndlr, dbProcessor, colNames, types);
            }
        }
        return instance;
    }

    public IndivScheduler(ConfigHandler cnfgHndlr, DBProcessor dbProcessor, String[] colNames, String[] types){
        this.dbProcessor = dbProcessor;
        this.colNames = colNames;
        this.types = types;
    }

    @Override
    public void runScheduler(ArrayList<ArrayList<String>> sqlData, Student[] students){
        // Assign students' data into a member function
        setStudents(students);

        // Generate a schedule table for each student.
        generateScheduleTable();

        insertIntoTable();

        System.out.println();
    }

    // Generate a schedule table for each student.
    @Override
    public void generateScheduleTable(){
        TimeUtil timeUtil = new TimeUtil();
        // TODO: Revert this back to the original
//        for (int i = 0; i < students.length; i++) {
        for (int i = 0; i < 1; i++) {
            Student curStudent = students[i];

            // Get assignment data related to the current student
            ArrayList<Assignment> curAssignments = curStudent.getAssignments();
            for (int j = 0; j < curAssignments.size(); j++) {

                Assignment curAssignment = curAssignments.get(j);
                for (int k = 0; k < curAssignment.getNumDays(); k++) {
                    // Find the earliest availability
                    Schedule earlistSchedule =
                            timeUtil.findEarliestAvailability(curStudent, curAssignment, k);
                    curStudent.addSchedule(earlistSchedule);
                }
            }
            students[i] = curStudent;
        }
    }

    @Override
    public void insertIntoTable() {
        String sql = "";

        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO " + tableName + " (");
        for (int i = 0; i < colNames.length; i++) {
            sb.append(colNames[i]);
            if (i < colNames.length-1)
                sb.append(", ");
        }
        if (sb.charAt(sb.length()-1) == ' ')
            sb.delete(sb.length()-2, sb.length());
        sb.append(") VALUES ");

        for (int i = 0; i < students.length; i++) {
            ArrayList<Schedule> schedules = students[i].getSchedules();
            for (int j = 0; j < schedules.size(); j++) {
                if (schedules.get(j) == null)
                    continue;

                // Collect data for each column
                DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                int id = (i*schedules.size() + j);
                int userID = schedules.get(j).getStudentID();
                int courseID = schedules.get(j).getCourseID();
                int assignmentID = schedules.get(j).getAssignmentID();
                String stTimeStr = fmt.print(schedules.get(j).getStartTime());
                String edTimeStr = fmt.print(schedules.get(j).getEndTime());
                // Default value
                int priority = -1;

                sb.append("(" + Integer.toString(id) + ", " + Integer.toString(userID) + ", " +
                            Integer.toString(courseID) + ", " + Integer.toString(assignmentID) +
                            ", '" + stTimeStr + "', '" + edTimeStr + "', " + priority + ")");
                if (j < schedules.size()-1)
                    sb.append(", ");
                else
                    sb.append(";");
            }
        }
        dbProcessor.runUpdateQuery(sb.toString());
    }

    public static void setStudents(Student[] students) {
        IndivScheduler.students = students;
    }

    public static Student[] getStudents() {
        return students;
    }
}
