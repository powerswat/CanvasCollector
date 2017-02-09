import org.json.simple.JSONArray;

/**
 * Created by powerswat on 2/7/17.
 */
public interface SQLFactory {
    public String makeCreateQuery(String pkCol);
    public String makeInsertQuery(JSONArray jsonArray, String tableName);
    public String makeUpdateQuery();
    public String makeDeleteQuery();
}
