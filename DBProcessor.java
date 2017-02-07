import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.*;

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
            if (rs != null)
                System.out.println("Database connection established");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private StringBuffer appendCols(ArrayList<String> cols, StringBuffer sb){

        return sb;
    }

    // Find a list of columns that do not have only null values
    private ArrayList<String> findNonEmptyColumns(ArrayList<String> cols, JSONArray data){
        ArrayList<String> nonEmptyCols = new ArrayList<String>();
        Hashtable<String, Integer> emptyMap = new Hashtable<String, Integer>();

        // Collect a list of columns that have at least one null value
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = (JSONObject) data.get(i);
            for (int j = 0; j < cols.size(); j++)
                if (jsonObject.get(cols.get(j)) == null)
                    if (emptyMap.containsKey(cols.get(j)))
                        emptyMap.put(cols.get(j), emptyMap.get(cols.get(j)) + 1);
                    else
                        emptyMap.put(cols.get(j), 1);
        }

        // List a set of columns that contain at least one meaningful value in it
        for (int i = 0; i < cols.size(); i++)
            if (!emptyMap.containsKey(cols.get(i)) || (emptyMap.get(cols.get(i)) < data.size()))
                nonEmptyCols.add(cols.get(i));

        return nonEmptyCols;
    }

    // Generate a CREATE TABLE query
    private StringBuffer makeCreateQuery(ArrayList<String> cols, JSONArray data,
                                         StringBuffer sb){
        // Check the type for each data element in the json format data
        ArrayList<String> types = checkTypes(cols, (JSONObject)data.get(0));
        for (int i = 0; i < cols.size(); i++) {
            if (types.get(i).equals(""))
                continue;
            sb.append(cols.get(i).toUpperCase() + " ");
//            if (i == cols.size()-1){
//                if (!pkCol.equals(""))
//                    sb.append(types.get(i).toUpperCase() + ", ");
//                else
//                    sb.append(types.get(i).toUpperCase() + ")");
//            } else
                sb.append(types.get(i).toUpperCase() + ", ");
        }

//        if (!pkCol.equals(""))
//            sb.append("PRIMARY KEY (" + pkCol.toUpperCase() + ")");

        return sb;
    }

    // List a set of columns in the given json data
    public ArrayList<String> extractJsonCols(JSONArray jsonArray){
        ArrayList<String> cols = new ArrayList<String>();
        Set keySet = ((JSONObject) jsonArray.get(0)).keySet();
        for (Iterator it = keySet.iterator(); it.hasNext();)
            cols.add((String) it.next());
        return cols;
    }

    // Generate a sql query to run
    @Override
    public String generateQuery(String queryType, JSONArray data,
                                String tableName) {
        StringBuffer sb = new StringBuffer();

        // Extract a list of columns from the given json data
        ArrayList<String> cols = extractJsonCols(data);

        // Find a list of columns that do not have only null values
        cols = findNonEmptyColumns(cols, data);

        sb.append(queryType + " ");
        if (queryType.toUpperCase().equals("CREATE")){
            sb.append("TABLE (");
            sb = makeCreateQuery(cols, data, sb);
        } else if (queryType.toUpperCase().equals("INSERT")){
            sb.append("INTO ");
            sb = appendCols(cols, sb);
        }

        return sb.toString();
    }

    @Override
    public void runQuery() {

    }

    // Check the type for each data element in the json format data
    private ArrayList<String> checkTypes(ArrayList<String> cols, JSONObject data){
        ArrayList<String> types = new ArrayList<String>();
        for (int i = 0; i < cols.size(); i++) {
            if (data.get(cols.get(i)) instanceof Integer ||
                    data.get(cols.get(i)) instanceof Long ||
                    data.get(cols.get(i)) instanceof Short)
                types.add("INT");
            else if (data.get(cols.get(i)) instanceof String ||
                    data.get(cols.get(i)) instanceof Character)
                types.add("VARHCAR");
            else if (data.get(cols.get(i)) instanceof Boolean)
                types.add("TINYINT(1)");
            else if (data.get(cols.get(i)) instanceof HashMap) {
                System.out.println("HashMap: " + cols.get(i));
                types.add("");
            }
            else if (data.get(cols.get(i)) instanceof JSONArray) {
                System.out.println("JSONArray: " + cols.get(i));
                types.add("");
            }
            else
                System.out.println("Missing types: " + cols.get(i));
        }
        return types;
    }

    @Override
    public void writeTable() {

    }
}
