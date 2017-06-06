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
    private static SQLProcessor sqlProcessor;
    private static IndivScheduler indivScheduler;
    private static TeamScheduler teamScheduler;

    private static String tableName = "SCHEDULES";
    private static String pkCol = "ID";
    private static String[] colNames = {"ID", "USER_ID", "COURSE_ID", "ASSIGNMENT_ID",
                                        "ASSIGNMENT_NAME", "START_TIME", "END_TIME", "PRIORITY"};
    private static String[] types = {"INT", "INT", "INT", "INT",
                                     "VARCHAR", "DATETIME", "DATETIME", "INT"};

    private static ArrayList<ArrayList<String>> sqlData;
    private static Student[] students;

    // Read necessary data from the database
    private static void getTableData(){
        // Retrieve assignment data
        String sql = "SELECT USER_ID, E.COURSE_ID, A.ID, A.NAME, A.CREATED_AT, " +
                "DUE_AT, POINTS_POSSIBLE " +
                "FROM ENROLLMENTS AS E INNER JOIN ASSIGNMENTS AS A " +
                "ON A.COURSE_ID = E.COURSE_ID " +
                "ORDER BY USER_ID, DUE_AT, POINTS_POSSIBLE, A.CREATED_AT, E.COURSE_ID;";
        String[] items = {"USER_ID", "E.COURSE_ID", "A.ID", "A.NAME", "A.CREATED_AT",
                            "DUE_AT", "POINTS_POSSIBLE"};
        sqlData = dbProcessor.runSelectQuery(sql, items);
    }

    // Create a table to store the schedules
    private static void createTable(){
        Hashtable<String, String> cols_types = new Hashtable<>();
        for (int i = 0; i < colNames.length; i++)
            cols_types.put(colNames[i], types[i]);

        SQLProcessor sqlProcessor = new SQLProcessor(tableName, pkCol);
        sqlProcessor.setCols_types(cols_types);
        String sql = sqlProcessor.makeCreateQuery();
        dbProcessor.runUpdateQuery(sql);
    }

    // Fill students' information
    private static void fillStudentCourseAssignmentInfo(){
        DataUtil<Integer> du = new DataUtil();

        Integer[] userIDs = new Integer[sqlData.size()];
        for (int i = 0; i < userIDs.length; i++)
            userIDs[i] = new Integer(sqlData.get(i).get(0));
        int numUser = du.countElem(userIDs);

        // Fill students' ID
        students = new Student[numUser];
        LinkedHashSet<Integer> uniqueSet = du.getUniqueSet();
        HashMap<Integer, Integer> mapUserIDtoIdx = new HashMap<>();
        int i = 0;
        for (Iterator it = uniqueSet.iterator(); it.hasNext();) {
            Integer userID = (Integer) it.next();
            students[i] = new Student(userID);
            mapUserIDtoIdx.put(userID, i++);
        }

        // Replace null in the data format with valid default values
        DataUtil<String> dataUtil = new DataUtil<>();
        ArrayList<Integer> missingIdcs = dataUtil.findMissingIndices(sqlData);
        sqlData = dataUtil.fillNull(sqlData, missingIdcs);

        // Fill students' courses and assignments information
        for (int j = 0; j < sqlData.size(); j++) {
            int userID = Integer.parseInt(sqlData.get(j).get(0));
            int userDataIdx = mapUserIDtoIdx.get(userID);
            Student curStudent = students[userDataIdx];
            curStudent.addCourseID(Integer.parseInt(sqlData.get(j).get(1)));

            Assignment assignment = new Assignment(Integer.parseInt(sqlData.get(j).get(2)),
                        Integer.parseInt(sqlData.get(j).get(1)),
                        sqlData.get(j).get(3), sqlData.get(j).get(4), sqlData.get(j).get(5),
                        Integer.parseInt(sqlData.get(j).get(6)));
            curStudent.addAssignment(assignment);
            students[userDataIdx] = curStudent;
        }
    }

    // Prepare data set to schedule
    public static void prepareData(){
        // Read the config file and parse it
        cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);

        // Read necessary data from the database
        getTableData();

        // Create Schedules table
        if (!dbProcessor.checkDupTable(tableName))
            createTable();

        // Fill students' course and assignment information
        fillStudentCourseAssignmentInfo();
    }

    public static ConfigHandler getCnfgHndlr() {
        return cnfgHndlr;
    }

    public static DBProcessor getDbProcessor() {
        return dbProcessor;
    }

    public static String[] getColNames() {
        return colNames;
    }

    public static String[] getTypes() {
        return types;
    }

    public static ArrayList<ArrayList<String>> getSqlData() {
        return sqlData;
    }

    public static Student[] getStudents() {
        return students;
    }



    public static void main(String[] args){
        // Prepare data set to schedule
        prepareData();

        // Schedule individual assignments
        indivScheduler = IndivScheduler.getInstance(cnfgHndlr, dbProcessor, colNames, types);
        indivScheduler.runScheduler(sqlData, students);

        System.out.println();
    }
}
