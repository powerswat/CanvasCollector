package db_integrity;

import configure.ConfigHandler;
import util.DBProcessor;

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

    // Replace null or empty values with valid default values.
    public static void placeDefaultValues(String tableName, String column, String curType){
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE " + tableName + " SET " + column + " = ");
        if (curType.toLowerCase().contains("char"))
            sb.append("'None' WHERE " + column + " IS NULL OR "
                    + column + " = '' OR " + column + " = 'null';");
        else if (curType.toLowerCase().contains("int") || curType.toLowerCase().contains("float")
                || curType.toLowerCase().contains("double"))
            sb.append("0 WHERE " + column + " IS NULL;");
        else
            return;
        dbProcessor.runUpdateQuery(sb.toString());
    }

    // Main driver
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
        for (int i = 0; i < tableNames.size(); i++) {
            for (ArrayList<String> column : tablesWithCols.get(i)) {
                String curType = column.get(1);
                if (curType.toLowerCase().contains("int") || curType.toLowerCase().contains("float") ||
                        curType.toLowerCase().contains("double") || curType.toLowerCase().contains("char"))
                    placeDefaultValues(tableNames.get(i), column.get(0), curType);
            }
        }
        String str = "COMMIT;";
        dbProcessor.runUpdateQuery(str);
        System.out.println();
    }

    public static DBProcessor getDbProcessor() {
        return dbProcessor;
    }

    public static void setDbProcessor(DBProcessor dbProcessor) {
        DBDataDriver.dbProcessor = dbProcessor;
    }
}
