package factories;

import java.util.ArrayList;

/**
 * Created by powerswat on 2/7/17.
 */
public interface SQLFactory {
    public String makeCreateQuery();
    public String makeInsertQuery();
    public String makeUpdateQuery();
    public String makeDeleteQuery();
    public String makeSelectQuery(String items);
}
