import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Array;
import java.util.*;

/**
 * Created by powerswat on 2/7/17.
 */
public class SQLProcessor implements SQLFactory {
    private static JSONArray data;
    private static ArrayList<String> cols = new ArrayList<String>();
    private static String tableName;

    public SQLProcessor(JSONArray data, String tableName){
        this.data = data;
        this.tableName = tableName;

        // Extract a list of columns from the given json data
        extractJsonCols();

        // Find a list of columns that do not have only null values
        findNonEmptyColumns();
    }

    // List a set of columns in the given json data
    private void extractJsonCols(){
        Set keySet = ((JSONObject) data.get(0)).keySet();
        for (Iterator it = keySet.iterator(); it.hasNext();)
            cols.add((String) it.next());
    }

    // Find a list of columns that do not have only null values
    private void findNonEmptyColumns(){
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

        cols = nonEmptyCols;
    }

    // Check the type for each data element in the json format data
    private ArrayList<String> checkTypes(ArrayList<String> cols, JSONArray data){
        ArrayList<String> types = new ArrayList<String>();
        JSONObject firstData = (JSONObject) data.get(0);
        for (int i = 0; i < cols.size(); i++) {
            String curCol = cols.get(i);
            if (firstData.get(curCol) instanceof Integer ||
                    firstData.get(curCol) instanceof Long ||
                    firstData.get(curCol) instanceof Short)
                types.add("INT");
            else if (firstData.get(curCol) instanceof String ||
                    firstData.get(curCol) instanceof Character) {
                // Count the maximum length of data in each column
                int dataLen = countMaxDataLen(cols.get(i), data);
                types.add("VARCHAR(" + dataLen + ")");
            }
            else if (firstData.get(curCol) instanceof Boolean)
                types.add("TINYINT(1)");
            else if (firstData.get(curCol) instanceof HashMap) {
                System.out.println("HashMap: " + cols.get(i));
                types.add("");
            }
            else if (firstData.get(curCol) instanceof JSONArray) {
                System.out.println("JSONArray: " + cols.get(i));
                types.add("");
            }
            else
                System.out.println("Missing types: " + cols.get(i));
        }
        return types;
    }

    // Count the maximum length of data in each column
    private int countMaxDataLen(String col, JSONArray data){
        Hashtable<String, Integer> lenMap = new Hashtable<String, Integer>();

        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = (JSONObject) data.get(i);
            String curStr = (String) jsonObject.get(col);
            if (!lenMap.containsKey(col))
                lenMap.put(col, curStr.length());
            else
                lenMap.put(col, Math.max(lenMap.get(col), curStr.length()));
        }

        return lenMap.get(col);
    }

    @Override
    public String makeCreateQuery(String pkCol) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE " + tableName + "(");

        // Check the type for each data element in the json format data
        ArrayList<String> types = checkTypes(cols, data);
        for (int i = 0; i < cols.size(); i++) {
            if (types.get(i).equals(""))
                continue;
            sb.append(cols.get(i).toUpperCase() + " ");
            if (i == cols.size()-1){
                if (!pkCol.equals(""))
                    sb.append(types.get(i).toUpperCase() + ", ");
                else
                    sb.append(types.get(i).toUpperCase() + ");");
            } else
            sb.append(types.get(i).toUpperCase() + ", ");
        }

        if (!pkCol.equals(""))
            sb.append("PRIMARY KEY (" + pkCol.toUpperCase() + "));");

        return sb.toString();
    }

    @Override
    public String makeInsertQuery() {
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    @Override
    public String makeUpdateQuery() {
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    @Override
    public String makeDeleteQuery() {
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }
}
