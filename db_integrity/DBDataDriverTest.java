package db_integrity;

import org.junit.BeforeClass;
import org.junit.Test;
import util.DBProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by powerswat on 6/6/17.
 */
public class DBDataDriverTest {
    private static DBDataDriver dbd = new DBDataDriver();

    @BeforeClass
    // Set up DB connection
    public static void setUpBeforeClass() throws Exception {
        dbd.connectDataBase();
    }

    @Test
    // Test DB connectivity
    public void testConnectDataBase() throws Exception {
        // Check whether the database connection returns something
        assertNotNull(dbd.getDbProcessor());
    }

    @Test
    // Test table name collection
    public void testGetTableNames() throws Exception {
        ArrayList<String> res = dbd.getTableNames();

        Set tableNames = new HashSet(res);
        assertTrue(tableNames.contains("ASSIGNMENTS"));
        assertTrue(tableNames.contains("COURSES"));
        assertTrue(tableNames.contains("USERS"));
        assertTrue(tableNames.contains("ENROLLMENTS"));
    }

    @Test
    // Test table column collection
    public void testGetTableColumns() throws Exception {
        ArrayList<String> tableNames = dbd.getTableNames();

        for (String tableName: tableNames) {
            ArrayList<ArrayList<String>> resTest = dbd.getTableColumns(tableName);

            String sql = "DESCRIBE " + tableName + ";";
            ArrayList<String> resReal = dbd.getDbProcessor().runSelectQuery(sql, "Field");

            Set realTblNames = new HashSet(resReal);
            for (ArrayList<String> columns : resTest)
                assertTrue(realTblNames.contains(columns.get(0)));
        }

    }
}