import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public class CourseETL implements CanvasETLFact {
    // Import necessary classes
    private static CourseETL instance = null;
    private static HTMLHandler htmlHandler = new HTMLHandler();
    private static FileProcessor fileProcessor = new FileProcessor();
    private static DBProcessor dbProcessor = new DBProcessor();
    private static ConfigHandler cnfgHndlr;

    private String jsonStr = "";
    private String coursePath = "courses";
    private String dataPath = "data/";

    public static CourseETL getInstance(ConfigHandler cnfgHandlr) {
        if (instance == null)
            synchronized (CourseETL.class) {
                if (instance == null)
                    instance = new CourseETL(cnfgHandlr);
            }
        return instance;
    }

    private CourseETL(ConfigHandler cnfgHandlr){
        this.cnfgHndlr = cnfgHandlr;
    }

    // Start collecting course information from the given token
    public void runProcess(String webApiAddr, String token){
        String url = webApiAddr + coursePath + "?access_token=" + token;
        readAPI(url);
        insertToDB();
    }

    @Override
    public void readAPI(String url) {
        String htmlStr = htmlHandler.readHTML(url);
        fileProcessor.writeFile(dataPath + "htmlStr.txt", htmlStr);
        JSONArray jsonArray = htmlHandler.parseToJson(dataPath + "htmlStr.txt");
    }

    @Override
    public void tabulate() {

    }

    @Override
    public void insertToDB() {
        dbProcessor.connectToDB(cnfgHndlr);
    }
}
