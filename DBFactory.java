/**
 * Created by powerswat on 1/27/17.
 */
public interface DBFactory {
    public void connectToDB(ConfigHandler cnfgHndlr);
    public void runQuery();
    public void writeTable();
}
