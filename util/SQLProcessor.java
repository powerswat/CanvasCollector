package util;

import factories.SQLFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import scheduler.Student;

import java.lang.reflect.Array;
import java.util.*;

public class SQLProcessor implements SQLFactory {
    private JSONArray data;
    private ArrayList<String> cols = new ArrayList<String>();
    private Hashtable<String, String> cols_types = new Hashtable<String, String>();
    private String tableName;
    private String pkCol;

    // Constructor used when the data is not provided
    public SQLProcessor(String tableName, String pkCol){
        this.tableName = tableName;
        this.pkCol = pkCol;
    }

    // Constructor used when the Json data is provided
    public SQLProcessor(JSONArray data, String tableName, String pkCol){
        this.data = data;
        this.tableName = tableName;
        this.pkCol = pkCol;

        SQLDataUtil sqlDataUtil = new SQLDataUtil();
        DataUtil dataUtil = new DataUtil();

        if (data.size() > 0) {
            // Extract a list of columns from the given json data
            extractJsonCols();
            // Find a list of columns that do not have only null values
            cols = dataUtil.extractNonEmptyColumns(data, cols);
            // Check the type for each data element in the json format data
            cols_types = sqlDataUtil.checkTypes(cols, data);
        }
    }

    // List a set of columns in the given json data
    private void extractJsonCols(){
        Set keySet = ((JSONObject) data.get(0)).keySet();
        for (Iterator it = keySet.iterator(); it.hasNext();)
            cols.add((String) it.next());
    }

    @Override
    public String makeCreateQuery() {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE " + tableName + "(");

        int i = 0;
        Set keySet = cols_types.keySet();
        for (Iterator it = keySet.iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (cols_types.get(key) == null || cols_types.get(key).equals(""))
                continue;
            sb.append(key.toUpperCase() + " ");
            if (i == cols.size()-1){
                if (!pkCol.equals(""))
                    sb.append(cols_types.get(key).toUpperCase() + ", ");
                else
                    sb.append(cols_types.get(key).toUpperCase() + ");");
            } else
            sb.append(cols_types.get(key).toUpperCase() + ", ");
            i++;
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
            if (cols_types.get(cols.get(i)) == null || cols_types.get(cols.get(i)).equals(""))
                continue;
            else
                sb.append(cols.get(i));

            if (i < cols.size()-1)
                sb.append(", ");
        }
        if (sb.charAt(sb.length()-1) == ' ')
            sb.delete(sb.length()-2, sb.length());
        sb.append(") VALUES ");

        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = (JSONObject) data.get(i);
            sb.append("(");

            for (int j = 0; j < cols.size(); j++) {
                if (cols_types.get(cols.get(j)) == null || cols_types.get(cols.get(j)).equals(""))
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
            if (sb.charAt(sb.length()-1) == ' ')
                sb.delete(sb.length()-2, sb.length());

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

    public void setCols_types(Hashtable<String, String> cols_types) {
        this.cols_types = cols_types;
    }
}
