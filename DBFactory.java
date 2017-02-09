import org.json.simple.JSONArray;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by powerswat on 1/27/17.
 */
public interface DBFactory {
    public void connectToDB(ConfigHandler cnfgHndlr);
    public void runQuery(String sql);
    public boolean checkDuplicate(String tableName, String dataType, String key);
}
