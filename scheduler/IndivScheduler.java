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

    public void runScheduler(){
        ArrayList<String[]> sqlData = getDBData();

        // Create table
        if (!dbProcessor.checkDupTable(tableName)) {
            String[] colNames
                    = {"ID", "USER_ID", "COURSE_ID", "ASSIGNMENT_ID", "START_TIME", "END_TIME"};
            String[] types = {"INT", "INT", "INT", "INT", "DATETIME", "DATETIME"};
            Hashtable<String, String> cols_types = new Hashtable<>();
            for (int i = 0; i < colNames.length; i++)
                cols_types.put(colNames[i], types[i]);
            createTable(cols_types);
        }

        // Plan based on constraints
        planSchedule(sqlData);

        System.out.println();
    }

    @Override
    public ArrayList<String[]> getDBData() {
        // Retrieve assignment data
        String sql = "SELECT USER_ID, E.COURSE_ID, A.ID, A.CREATED_AT, DUE_AT " +
                    "FROM ENROLLMENTS AS E INNER JOIN ASSIGNMENTS AS A " +
                    "ON A.COURSE_ID = E.COURSE_ID " +
                    "ORDER BY USER_ID, DUE_AT, A.CREATED_AT, E.COURSE_ID;";
        String[] items = {"USER_ID", "E.COURSE_ID", "A.ID", "A.CREATED_AT", "DUE_AT"};
        ArrayList<String[]> sqlData = dbProcessor.runSelectQuery(sql, items);

        return sqlData;
    }

    @Override
    public void createTable(Hashtable<String, String> col_types) {
        SQLProcessor sqlProcessor = new SQLProcessor(tableName, pkCol);
        sqlProcessor.setCols_types(col_types);
        String sql = sqlProcessor.makeCreateQuery();
        dbProcessor.runUpdateQuery(sql);
    }

    @Override
    public void planSchedule(ArrayList<String[]> sqlData) {


        System.out.println();
    }

    @Override
    public void insertTable() {

    }
}
