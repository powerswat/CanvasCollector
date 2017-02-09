import org.json.simple.JSONArray;

/**
 * Created by powerswat on 2/7/17.
 */
public interface SQLFactory {
    public String makeCreateQuery();
    public String makeInsertQuery();
    public String makeUpdateQuery();
    public String makeDeleteQuery();
}
