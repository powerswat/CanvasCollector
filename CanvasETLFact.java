import org.json.simple.JSONArray;

/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public interface CanvasETLFact {
    public static WebTextHandler webTextHandler = new WebTextHandler();
    public static FileProcessor fileProcessor = new FileProcessor();
    public String dataPath = "data/";

    public void runProcess(String webApiAddr, String token);
    public void readAPI(String url);
    public void insertToDB();
}
