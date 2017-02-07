import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public class CourseETL implements CanvasETLFact {
    // Import necessary classes
    private static CourseETL instance = null;
    private static ConfigHandler cnfgHndlr;
    private static JSONArray jsonArray;
    private static ArrayList<String> jsonCols;
    private static DBProcessor dbProcessor;
    private static String coursePath = "courses";
    private static String pkCol = "id";

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
        String url = webApiAddr + coursePath + "?access_token=" + token;
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
        // Generate a sql query to create a table
        String sql = dbProcessor.generateQuery("CREATE", jsonArray,
                            dataPath.toUpperCase());
    }
}
