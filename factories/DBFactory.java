package factories;

import configure.ConfigHandler;

import java.util.ArrayList;

/**
 * Created by powerswat on 1/27/17.
 */
public interface DBFactory {
    public void connectToDB(ConfigHandler cnfgHndlr);
    public void runUpdateQuery(String sql);
    public ArrayList<String> runSelectQuery(String sql, String items);
    public ArrayList<ArrayList<String>> runSelectQuery(String sql, String[] items);
    public boolean checkDupTable(String tableName);
}
