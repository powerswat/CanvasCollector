package factories;

import org.json.simple.JSONArray;

/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public interface CanvasETLFact {
    public void runProcess(String webApiAddr, String token);
    public JSONArray readAPI(String url);
    public void insertToDB(JSONArray jsonArray);
    public void createTable(JSONArray jsonArray);
    public JSONArray removeDuplicateData(JSONArray jsonArray);
}
