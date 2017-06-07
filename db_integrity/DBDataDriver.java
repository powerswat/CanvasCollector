package db_integrity;

import configure.ConfigHandler;
import util.DBProcessor;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by powerswat on 6/6/17.
 */
public class DBDataDriver {
    private static DBProcessor dbProcessor = new DBProcessor();

    // Connect to DB
    public static void connectDataBase(){
        // Read the config file and parse it
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);
    }

    // Get the names of each table in the DB
    public static ArrayList<String> getTableNames(){
        String sql = "SHOW TABLES;";
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, "Tables_in_canvas");

        return res;
    }

    // Get the name and type of each column in the table
    public static ArrayList<ArrayList<String>> getTableColumns(String tableName){
        String sql = "DESCRIBE " +  tableName + ";";
        String[] columns = {"Field", "Type"};
        ArrayList<ArrayList<String>> res = dbProcessor.runSelectQuery(sql, columns);
        return res;
    }

    public static void main(String[] args){
        // Connect to DB
        connectDataBase();

        // Get the names of each table in the DB
        ArrayList<String> tableNames = getTableNames();

        // Get the name and type of each column in the table
        ArrayList<ArrayList<ArrayList<String>>> tablesWithCols = new ArrayList<>();
        for (String tableName : tableNames)
            tablesWithCols.add(getTableColumns(tableName));

        // Replace null or empty values in the table with given default values
    }

    public static DBProcessor getDbProcessor() {
        return dbProcessor;
    }
}
