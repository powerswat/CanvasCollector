import org.json.simple.JSONArray;

import java.util.ArrayList;

/**
 * Created by powerswat on 1/27/17.
 */
public interface DBFactory {
    public void connectToDB(ConfigHandler cnfgHndlr);
    public String generateQuery(String queryType, JSONArray data, String tableName);
    public void runQuery();
    public void writeTable();
}
