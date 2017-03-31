package scheduler;

import configure.ConfigHandler;
import util.DBProcessor;
import util.DataUtil;
import util.SQLProcessor;

import java.util.*;

/**
 * Created by powerswat on 3/8/17.
 */
public class StudyScheduleDriver {
    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor = new DBProcessor();
    private static IndivScheduler indivScheduler;
    private static TeamScheduler teamScheduler;

    private static String tableName = "SCHEDULES";
    private static String pkCol = "ID";

    private static ArrayList<String[]> sqlData;
    private static Student[] students;

    // Read necessary data from the database
    private static void getTableData(){
        // Retrieve assignment data
        String sql = "SELECT USER_ID, E.COURSE_ID, A.ID, A.NAME, A.CREATED_AT, DUE_AT " +
                "FROM ENROLLMENTS AS E INNER JOIN ASSIGNMENTS AS A " +
                "ON A.COURSE_ID = E.COURSE_ID " +
                "ORDER BY USER_ID, DUE_AT, A.CREATED_AT, E.COURSE_ID;";
        String[] items = {"USER_ID", "E.COURSE_ID", "A.ID", "A.NAME", "A.CREATED_AT", "DUE_AT"};
        sqlData = dbProcessor.runSelectQuery(sql, items);
    }

    // Create a table to store the schedules
    private static void createTable(){
        String[] colNames
                    = {"ID", "USER_ID", "COURSE_ID", "ASSIGNMENT_ID", "START_TIME", "END_TIME"};
        String[] types = {"INT", "INT", "INT", "INT", "DATETIME", "DATETIME"};
        Hashtable<String, String> cols_types = new Hashtable<>();
        for (int i = 0; i < colNames.length; i++)
            cols_types.put(colNames[i], types[i]);

        SQLProcessor sqlProcessor = new SQLProcessor(tableName, pkCol);
        sqlProcessor.setCols_types(cols_types);
        String sql = sqlProcessor.makeCreateQuery();
        dbProcessor.runUpdateQuery(sql);
    }

    // Fill students' information
    private static void fillStudentInfo(){
        DataUtil<Integer> du = new DataUtil();

        Integer[] userIDs = new Integer[sqlData.size()];
        for (int i = 0; i < userIDs.length; i++)
            userIDs[i] = new Integer(sqlData.get(i)[0]);
        int numUser = du.countElem(userIDs);

        students = new Student[numUser];
        LinkedHashSet<Integer> uniqueSet = du.getUniqueSet();
        int i = 0;
        // TODO: Redefine dues for tasks
        for (Iterator it = uniqueSet.iterator(); it.hasNext();) {
            students[i++] = new Student((Integer) it.next());
            System.out.println();
        }
        System.out.println();
    }

    // Prepare data set to schedule
    private static void prepareData(){
        // Read the config file and parse it
        cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);

        // Read necessary data from the database
        getTableData();

        // Create Schedules table
        if (!dbProcessor.checkDupTable(tableName))
            createTable();

        // Fill students' information
        fillStudentInfo();

    }

    public static void main(String[] args){
        // Prepare data set to schedule
        prepareData();

        // Schedule individual tasks
        indivScheduler = IndivScheduler.getInstance(cnfgHndlr, dbProcessor);
        indivScheduler.runScheduler(sqlData);

        // Schedule individual tasks

        System.out.println();
    }
}
