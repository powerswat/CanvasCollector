package db_integrity;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import util.DBProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by powerswat on 6/6/17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DBDataDriverTest extends TestCase {
    private static DBDataDriver dbd = new DBDataDriver();
    private static ArrayList<String> tableNames = new ArrayList<>();
    private static ArrayList<ArrayList<ArrayList<String>>> tablesWithCols = new ArrayList<>();

    @Before
    // Set up DB connection
    public void setUp() throws Exception {
        dbd.connectDataBase();
    }

    @Test
    // Test DB connectivity
    public void test_1_connectDataBase() throws Exception {
        // Check whether the database connection returns something
        assertNotNull(dbd.getDbProcessor());
    }

    @Test
    // Test table name collection
    public void test_2_getTableNames() throws Exception {
        tableNames = dbd.getTableNames();

        Set tableNameSet = new HashSet(tableNames);
        assertTrue(tableNameSet.contains("ASSIGNMENTS"));
        assertTrue(tableNameSet.contains("COURSES"));
        assertTrue(tableNameSet.contains("USERS"));
        assertTrue(tableNameSet.contains("ENROLLMENTS"));
    }

    @Test
    // Test table column collection
    public void test_3_getTableColumns() throws Exception {
        for (String tableName: tableNames) {
            ArrayList<ArrayList<String>> resTest = dbd.getTableColumns(tableName);
            tablesWithCols.add(resTest);

            String sql = "DESCRIBE " + tableName + ";";
            ArrayList<String> resReal = dbd.getDbProcessor().runSelectQuery(sql, "Field");

            Set realTblNames = new HashSet(resReal);
            for (ArrayList<String> columns : resTest)
                assertTrue(realTblNames.contains(columns.get(0)));
        }
    }

    @Test
    // Test the correctness of valid default value insertion
    public void test_4_placeDefaultValues() throws Exception {
        // Replace null or empty values in the table with given default values
        for (int i = 0; i < tableNames.size(); i++) {
            for (ArrayList<String> column : tablesWithCols.get(i)) {
                String curType = column.get(1);
                if (curType.toLowerCase().contains("int") || curType.toLowerCase().contains("float") ||
                        curType.toLowerCase().contains("double") || curType.toLowerCase().contains("char")){
                    dbd.placeDefaultValues(tableNames.get(i), column.get(0), curType);
                    String sql = "";
                    if (curType.toLowerCase().contains("char")) {
                        sql = "SELECT COUNT(*) FROM " + tableNames.get(i) + " WHERE "
                                + column.get(0) + " IS NULL OR " + column.get(0) + " = '';";
                        ArrayList<String> res = dbd.getDbProcessor().runSelectQuery(sql,"COUNT(*)");
                        if (res != null)
                            assertEquals("0", res.get(0));
                    }
                }
            }
        }
    }
}