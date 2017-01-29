import java.sql.*;

/**
 * Created by powerswat on 1/28/17.
 */
public class DBProcessor implements DBFactory{

    @Override
    public void connectToDB(ConfigHandler cnfgHndlr) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(
                    cnfgHndlr.getDbAddr(), cnfgHndlr.getDbAcct(), cnfgHndlr.getDbPass());
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SHOW DATABASES");
            if (st.execute("SHOW DATABASES"))
                rs = st.getResultSet();
            while (rs.next())
                System.out.println(rs.getNString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runQuery() {

    }

    @Override
    public void writeTable() {

    }
}
