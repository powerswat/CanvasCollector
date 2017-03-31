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
 * Created by powerswat on 2/23/17.
 */
public class TaskETL implements CanvasETLFact{
    public static WebTextHandler webTextHandler = new WebTextHandler();
    public static FileProcessor fileProcessor = new FileProcessor();
    public String dataPath = "data/";

    private static TaskETL instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;

    private static String tableName1st = "COURSES";
    private static String tableName2nd = "ASSIGNMENTS";
    private static String pkCol = "ID";

    public static TaskETL getInstance(ConfigHandler cnfgHndlr, DBProcessor dbProcessor){
        if (instance == null)
            synchronized (TaskETL.class){
                if (instance == null)
                    instance = new TaskETL(cnfgHndlr, dbProcessor);
            }
        return instance;
    }

    public TaskETL(ConfigHandler cnfgHndlr, DBProcessor dbProcessor){
        this.cnfgHndlr = cnfgHndlr;
        this.dbProcessor = dbProcessor;
    }

    @Override
    public void runProcess(String webApiAddr, String token) {
        SQLProcessor sqlProcessor = new SQLProcessor(new JSONArray(), tableName1st, pkCol);
        String sql = sqlProcessor.makeSelectQuery(pkCol);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, pkCol);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < res.size(); i++) {
            String url = webApiAddr + tableName1st.toLowerCase() + "/" + res.get(i) + "/"
                    + tableName2nd.toLowerCase() + "?access_token=" + token;
            sb.append(readAPI(url));
        }
        fileProcessor.writeFile(dataPath + "htmlStr.txt",
                sb.toString().replaceAll("\\]\\[", ""));
        JSONArray jsonArray = parseJson(dataPath + "htmlStr.txt");
        insertToDB(jsonArray);
    }

    @Override
    public String readAPI(String url) {
        return webTextHandler.readHTML(url);
    }

    @Override
    public JSONArray parseJson(String filename) {
        return webTextHandler.parseToJson(filename);
    }

    @Override
    public void insertToDB(JSONArray jsonArray) {
        String sql = "";
        // Generate a sql query to create a table
        if (!dbProcessor.checkDupTable(tableName2nd))
            createTable(jsonArray);

        // Remove duplicate data elements from the given Json array based on the given unique key set
        jsonArray = webTextHandler.removeDuplicateJson(jsonArray);
        jsonArray = removeDuplicateDataInDB(jsonArray);

        // Auto fill time order for the tasks (Fill null time and unreasonable time)
        jsonArray = webTextHandler.autoFillTimeFormat(jsonArray);

        // Insert the collected data into the designated table
        if (jsonArray.size() > 0) {
            SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
            sql = sqlProcessor.makeInsertQuery();
            dbProcessor.runUpdateQuery(sql);
        }
    }

    @Override
    public void createTable(JSONArray jsonArray) {
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
        String sql = sqlProcessor.makeCreateQuery();
        dbProcessor.runUpdateQuery(sql);
    }

    @Override
    public JSONArray removeDuplicateDataInDB(JSONArray jsonArray) {
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
        String sql = sqlProcessor.makeSelectQuery(pkCol);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, pkCol);
        jsonArray = webTextHandler.removeDuplicatesInDB(jsonArray, tableName2nd, pkCol, dbProcessor);
        return jsonArray;
    }
}
