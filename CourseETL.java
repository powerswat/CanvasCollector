import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public class CourseETL implements CanvasETLFact {
    private static CourseETL instance = null;
    private static HTMLHandler htmlHandler = new HTMLHandler();
    private static FileProcessor fileProcessor = new FileProcessor();

    private String jsonStr = "";
    private String coursePath = "courses";
    private String dataPath = "data/";

    public static CourseETL getInstance() {
        if (instance == null)
            synchronized (CourseETL.class) {
                if (instance == null)
                    instance = new CourseETL();
            }
        return instance;
    }

    // Start collecting course information from the given token
    public void runProcess(String webApiAddr, String token){
        String url = webApiAddr + coursePath + "?access_token=" + token;
        readAPI(url);
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

    }
}
