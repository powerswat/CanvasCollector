package data_collector;

import configure.ConfigHandler;
import factories.CanvasETLFact;
import org.json.simple.JSONArray;
import util.DBProcessor;
import util.FileProcessor;
import util.SQLProcessor;
import util.WebTextHandler;

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
        String htmlStr = readAPI(url);
        fileProcessor.writeFile(dataPath + "htmlStr.txt", htmlStr);

        JSONArray jsonArray = parseJson(dataPath + "htmlStr.txt");
        insertToDB(jsonArray);
    }

    @Override
    public String readAPI(String url) {
        return webTextHandler.readHTML(url);
    }

    @Override
    public JSONArray parseJson(String filename) {
        return webTextHandler.parseToJson(dataPath + "htmlStr.txt");
    }

    @Override
    public void insertToDB(JSONArray jsonArray) {
        String sql = "";
        // Generate a sql query to create a table
        if (!dbProcessor.checkDupTable(tableName))
            createTable(jsonArray);

        // Insert the collected data into the designated table
        jsonArray = webTextHandler.removeDuplicateJson(jsonArray);
        jsonArray = removeDuplicateDataInDB(jsonArray);
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
    public JSONArray removeDuplicateDataInDB(JSONArray jsonArray) {
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName, pkCol);
        String sql = sqlProcessor.makeSelectQuery(pkCol);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, pkCol);
        jsonArray = webTextHandler.removeDuplicatesInDB(jsonArray, tableName, pkCol, dbProcessor);
        return jsonArray;
    }
}
