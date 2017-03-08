import factories.SQLFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by powerswat on 2/7/17.
 */
public class SQLProcessor implements SQLFactory {
    private JSONArray data;
    private ArrayList<String> cols = new ArrayList<String>();
    private Hashtable<String, String> cols_types = new Hashtable<String, String>();
    private String tableName;
    private String pkCol;

    public SQLProcessor(JSONArray data, String tableName, String pkCol){
        this.data = data;
        this.tableName = tableName;
        this.pkCol = pkCol;

        if (data.size() > 0) {
            // Extract a list of columns from the given json data
            extractJsonCols();
            // Find a list of columns that do not have only null values
            findNonEmptyColumns();
            // Check the type for each data element in the json format data
            checkTypes(cols, data);
        }
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

    // Check whether the current string data represents time
    private boolean checkTimeFormat(JSONArray data, String curCol){
        JSONObject item = (JSONObject) data.get(0);
        String curData = (String) item.get(curCol);
        if (curData != null) {
            String signature = curData.replaceAll("[^-:TZ0-9]", "");
            if (curData.length() == signature.length())
                return true;
        }
        return false;
    }

    // Check the type for each data element in the json format data
    private void checkTypes(ArrayList<String> cols, JSONArray data){
        for (int j = 0; j < data.size(); j++) {
            JSONObject curData = (JSONObject) data.get(j);
            for (int i = 0; i < cols.size(); i++) {
                String curCol = cols.get(i);
                // For integer data
                if (curData.get(curCol) instanceof Integer ||
                        curData.get(curCol) instanceof Long ||
                        curData.get(curCol) instanceof Short)
                    cols_types.put(curCol, "INT");
                    // For String or Datetime data
                else if (curData.get(curCol) instanceof String ||
                        curData.get(curCol) instanceof Character) {
                    // Count the maximum length of data in each column
                    int dataLen = countMaxDataLen(cols.get(i), data);

                    if (checkTimeFormat(data, curCol))
                        cols_types.put(curCol, "DATETIME");
                    else
                        cols_types.put(curCol, "VARCHAR(" + dataLen + ")");
                }
                // For boolean data
                else if (curData.get(curCol) instanceof Boolean)
                    cols_types.put(curCol, "TINYINT(1)");
                    // For HashMap data
                else if (curData.get(curCol) instanceof HashMap) {
                    System.out.println("HashMap: " + cols.get(i));
                    cols_types.put(curCol, "");
                }
                // For nested JSONArray data
                else if (curData.get(curCol) instanceof JSONArray) {
                    System.out.println("JSONArray: " + cols.get(i));
                    cols_types.put(curCol, "");
                } else
                    System.out.println("Missing types: " + cols.get(i));
            }
        }
    }

    // Count the maximum length of data in each column
    private int countMaxDataLen(String col, JSONArray data){
        Hashtable<String, Integer> lenMap = new Hashtable<String, Integer>();

        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = (JSONObject) data.get(i);
            String curStr = (String) jsonObject.get(col);
            if (curStr == null)
                continue;
            if (!lenMap.containsKey(col))
                lenMap.put(col, curStr.length());
            else
                lenMap.put(col, Math.max(lenMap.get(col), curStr.length()));
        }

        return lenMap.get(col);
    }

    @Override
    public String makeCreateQuery() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE " + tableName + "(");

        for (int i = 0; i < cols.size(); i++) {
            if (i == 5)
                System.out.println(i);
            if (cols_types.get(cols.get(i)) == null || cols_types.get(cols.get(i)).equals(""))
                continue;
            sb.append(cols.get(i).toUpperCase() + " ");
            if (i == cols.size()-1){
                if (!pkCol.equals(""))
                    sb.append(cols_types.get(cols.get(i)).toUpperCase() + ", ");
                else
                    sb.append(cols_types.get(cols.get(i)).toUpperCase() + ");");
            } else
            sb.append(cols_types.get(cols.get(i)).toUpperCase() + ", ");
        }

        if (!pkCol.equals(""))
            sb.append("PRIMARY KEY (" + pkCol.toUpperCase() + "));");

        return sb.toString();
    }

    @Override
    public String makeInsertQuery() {
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO " + tableName + " (");

        for (int i = 0; i < cols.size(); i++) {
            if (cols_types.get(cols.get(i)) == null ||
                    cols_types.get(cols.get(i)).equals("") /*||
                    missingTypes.contains(cols.get(j))*/)
                continue;
            else
                sb.append(cols.get(i));

            if (i < cols.size()-1)
                sb.append(", ");
        }
        sb.append(") VALUES ");

        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = (JSONObject) data.get(i);
            sb.append("(");

            for (int j = 0; j < cols.size(); j++) {
                if (cols_types.get(cols.get(j)) == null ||
                        cols_types.get(cols.get(j)).equals("") /*||
                        missingTypes.contains(cols.get(j))*/)
                    continue;
                else if (cols_types.get(cols.get(j)).contains("VARCHAR")) {
                    String str = (String) jsonObject.get(cols.get(j));
                    if (str == null)
                        str = "null";
                    str = str.replaceAll("'", "");
                    sb.append("'" + str + "'");
                }
                else if (cols_types.get(cols.get(j)).contains("DATETIME")) {
                    String timeStr = (String) jsonObject.get(cols.get(j));
                    timeStr = timeStr.replaceAll("[TZ]", " ");
                    timeStr = timeStr.trim();
                    sb.append("'" + timeStr + "'");
                }
                else
                    sb.append(jsonObject.get(cols.get(j)));

                if (j < cols.size()-1)
                    sb.append(", ");
            }
            if (i == data.size()-1)
                sb.append(");");
            else
                sb.append("), ");
        }
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

    @Override
    public String makeSelectQuery(String items) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT " + items + " FROM " + tableName);
        return sb.toString();
    }

    public Hashtable<String, String> getCols_types() {
        return cols_types;
    }
}
