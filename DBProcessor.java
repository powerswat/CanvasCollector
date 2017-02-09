import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.*;

/**
 * Created by powerswat on 1/28/17.
 */
public class DBProcessor implements DBFactory{
    private static Connection con = null;
    private static Statement st = null;
    private static ResultSet rs = null;

    @Override
    public void connectToDB(ConfigHandler cnfgHndlr) {
        try {
            con = DriverManager.getConnection(
                    cnfgHndlr.getDbAddr(), cnfgHndlr.getDbAcct(), cnfgHndlr.getDbPass());
            st = con.createStatement();
            rs = st.executeQuery("SHOW DATABASES");
            if (rs != null)
                System.out.println("Database connection established");
            rs = st.executeQuery("USE " + cnfgHndlr.getDbName());
            if (rs != null)
                System.out.println("Database " + cnfgHndlr.getDbName() + " selected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runQuery(String sql) {
        try {
            st = con.createStatement();
            int affected_rows = st.executeUpdate(sql);
            System.out.println(affected_rows + " rows affected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkDuplicate(String tableName, String dataType, String key) {
        try {
            if (dataType.equals("table"))
                rs = st.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
            else if (dataType.equals("data"))
                rs = st.executeQuery("SHOW TABLES LIKE '" + key + "'");
            else {
                System.out.println("Data type must be either 'table' or 'data'. Your change has not been updated.");
                return true;
            }
            if (rs.next())
                return true;
            else
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
