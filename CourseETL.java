import configure.ConfigHandler;
import factories.CanvasETLFact;
import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public class CourseETL implements CanvasETLFact {
    public static WebTextHandler webTextHandler = new WebTextHandler();
    public static FileProcessor fileProcessor = new FileProcessor();
    public String dataPath = "data/";

    // Import necessary classes
    private static CourseETL instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;

    private static String tableName = "COURSES";
    private static String pkCol = "ID";

    public static CourseETL getInstance(ConfigHandler cnfgHandlr, DBProcessor dbProcessor) {
        if (instance == null)
            synchronized (CourseETL.class) {
                if (instance == null)
                    instance = new CourseETL(cnfgHandlr, dbProcessor);
            }
        return instance;
    }

    private CourseETL(ConfigHandler cnfgHandlr, DBProcessor dbProcessor){
        this.cnfgHndlr = cnfgHandlr;
        this.dbProcessor = dbProcessor;
    }

    // Start collecting course information from the given token
    @Override
    public void runProcess(String webApiAddr, String token){
        String url = webApiAddr + tableName.toLowerCase() + "?access_token=" + token;
        JSONArray jsonArray = readAPI(url);
        insertToDB(jsonArray);
    }

    @Override
    public JSONArray readAPI(String url) {
        String htmlStr = webTextHandler.readHTML(url);
        fileProcessor.writeFile(dataPath + "htmlStr.txt", htmlStr);
        // Parse a single string value of json into a structured json format
        JSONArray jsonArray = webTextHandler.parseToJson(dataPath + "htmlStr.txt");
        return jsonArray;
    }

    @Override
    public void insertToDB(JSONArray jsonArray) {
        String sql = "";
        // Generate a sql query to create a table
        if (!dbProcessor.checkDupTable(tableName))
            createTable(jsonArray);

        // Insert the collected data into the designated table
        // TODO: Check duplicates
        jsonArray = removeDuplicateData(jsonArray);
        if (jsonArray.size() > 0) {
            SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName, pkCol);
            sql = sqlProcessor.makeInsertQuery();
            dbProcessor.runUpdateQuery(sql);
        }
    }

    @Override
    public void createTable(JSONArray jsonArray) {
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName, pkCol);
        String sql = sqlProcessor.makeCreateQuery();
        dbProcessor.runUpdateQuery(sql);
    }

    @Override
    public JSONArray removeDuplicateData(JSONArray jsonArray) {
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName, pkCol);
        String sql = sqlProcessor.makeSelectQuery(pkCol);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, pkCol);
        jsonArray = webTextHandler.removeDuplicates(jsonArray, tableName, pkCol, dbProcessor);
        return jsonArray;
    }
}
