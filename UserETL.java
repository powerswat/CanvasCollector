import configure.ConfigHandler;
import factories.CanvasETLFact;
import org.json.simple.JSONArray;
import java.util.ArrayList;


/**
 * Created by powerswat on 2/9/17.
 */
public class UserETL implements CanvasETLFact {
    public static WebTextHandler webTextHandler = new WebTextHandler();
    public static FileProcessor fileProcessor = new FileProcessor();
    public String dataPath = "data/";

    // Import necessary classes
    private static UserETL instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;

    private static String tableName1st = "COURSES";
    private static String tableName2nd = "USERS";
    private static String pkCol = "ID";

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
        SQLProcessor sqlProcessor = new SQLProcessor(new JSONArray(), tableName1st, pkCol);
        String sql = sqlProcessor.makeSelectQuery(pkCol);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, pkCol);
        for (int i = 0; i < res.size(); i++) {
            String url = webApiAddr + tableName1st.toLowerCase() + "/" + res.get(i) + "/" + tableName2nd.toLowerCase()
                    + "?access_token=" + token;
            JSONArray jsonArray = readAPI(url);
            insertToDB(jsonArray);
        }
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
    public void createTable(JSONArray jsonArray){
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
        String sql = sqlProcessor.makeCreateQuery();
        dbProcessor.runUpdateQuery(sql);
    }

    @Override
    public JSONArray removeDuplicateData(JSONArray jsonArray){
        SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
        String sql = sqlProcessor.makeSelectQuery(pkCol);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, pkCol);
        jsonArray = webTextHandler.removeDuplicates(jsonArray, tableName2nd, pkCol, dbProcessor);
        return jsonArray;
    }

    @Override
    public void insertToDB(JSONArray jsonArray) {
        String sql = "";
        // Generate a sql query to create a table
        if (!dbProcessor.checkDupTable(tableName2nd))
            createTable(jsonArray);

        // Remove duplicate data elements from the given Json array based on the given unique key set
        jsonArray = removeDuplicateData(jsonArray);

        // Insert the collected data into the designated table
        if (jsonArray.size() > 0) {
            SQLProcessor sqlProcessor = new SQLProcessor(jsonArray, tableName2nd, pkCol);
            sql = sqlProcessor.makeInsertQuery();
            dbProcessor.runUpdateQuery(sql);
        }
    }
}
