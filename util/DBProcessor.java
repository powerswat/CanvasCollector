package util;

import configure.ConfigHandler;
import factories.DBFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by powerswat on 1/28/17.
 */
public class DBProcessor implements DBFactory {
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
    public void runUpdateQuery(String sql) {
        try {
            st = con.createStatement();
            int affected_rows = st.executeUpdate(sql);
            System.out.println(affected_rows + " rows affected.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> runSelectQuery(String sql, String items) {
        ArrayList<String> res = new ArrayList<String>();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()){
                res.add(rs.getString(items));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }
    
    @Override
    public ArrayList<ArrayList<String>> runSelectQuery(String sql, String[] items) {
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()){
                ArrayList<String> template = new ArrayList<String>();
                for (int i = 0; i < items.length; i++)
                    template.add(rs.getString(items[i]));
                res.add(template);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean checkDupTable(String tableName) {
        try {
            rs = st.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
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
