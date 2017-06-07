package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by powerswat on 6/6/17.
 */
public class SQLDataUtil {
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

    // Check whether the current string data represents time
    private boolean checkTimeFormat(JSONObject item, String curCol){
        String curData = (String) item.get(curCol);
        if (curData != null) {
            String signature = curData.replaceAll("[^-:TZ0-9]", "");
            if (curData.length() == signature.length())
                return true;
        }
        return false;
    }

    // Check the type for each data element in the json format data
    public Hashtable<String, String> checkTypes(ArrayList<String> cols, JSONArray data){
        Hashtable<String, String> cols_types = new Hashtable<String, String>();

        for (int j = 0; j < data.size(); j++) {
            JSONObject curData = (JSONObject) data.get(j);
            for (int i = 0; i < cols.size(); i++) {
                String curCol = cols.get(i);
                if (cols_types.get(curCol) != null)
                    continue;

                // For integer data
                if (curData.get(curCol) instanceof Integer ||
                        curData.get(curCol) instanceof Long ||
                        curData.get(curCol) instanceof Short) {
                    cols_types.put(curCol, "INT");
                }
                // For floating point data
                else if (curData.get(curCol) instanceof Float ||
                        curData.get(curCol) instanceof Double) {
                    cols_types.put(curCol, "FLOAT");
                }
                // For String or Datetime data
                else if (curData.get(curCol) instanceof String ||
                        curData.get(curCol) instanceof Character) {
                    // Count the maximum length of data in each column
                    int dataLen = countMaxDataLen(cols.get(i), data);

                    if (checkTimeFormat(curData, curCol))
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

        return cols_types;
    }
}
