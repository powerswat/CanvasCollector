import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * Created by powerswat on 2/9/17.
 */
public class UserETL implements CanvasETLFact {
    // Import necessary classes
    private static UserETL instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;
    private static SQLProcessor sqlProcessor;

    private static JSONArray jsonArray;
    private static ArrayList<String> jsonCols;
    private static String tableName1st = "COURSES";
    private static String tableName2nd = "USERS";
    private static String pkCol = "id";

    public static UserETL getInstance(ConfigHandler cnfgHandlr, DBProcessor dbProcessor) {
        if (instance == null)
            synchronized (UserETL.class) {
                if (instance == null)
                    instance = new UserETL(cnfgHandlr, dbProcessor);
            }
        return instance;
    }

    private UserETL(ConfigHandler cnfgHandlr, DBProcessor dbProcessor){
        this.cnfgHndlr = cnfgHandlr;
        this.dbProcessor = dbProcessor;
    }

    @Override
    public void runProcess(String webApiAddr, String token) {
        // TODO: Implement a module to iterate over all the courses in the database.
        String url = webApiAddr + tableName1st.toLowerCase() + "/2/" + tableName2nd.toLowerCase()
                        + "?access_token=" + token;
        readAPI(url);
        insertToDB();
    }

    @Override
    public void readAPI(String url) {
        String htmlStr = webTextHandler.readHTML(url);
        fileProcessor.writeFile(dataPath + "htmlStr.txt", htmlStr);
        // Parse a single string value of json into a structured json format
        jsonArray = webTextHandler.parseToJson(dataPath + "htmlStr.txt");
    }

    @Override
    public void insertToDB() {
        sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
        String sql = "";
        // Generate a sql query to create a table
        if (!dbProcessor.checkDuplicate(tableName2nd, "table", "")) {
            sql = sqlProcessor.makeCreateQuery();
            dbProcessor.runQuery(sql);
        }
        // Insert the collected data into the designated table
        // TODO: Check duplicates
        sql = sqlProcessor.makeInsertQuery();
        dbProcessor.runQuery(sql);
        System.out.println();
    }
}
