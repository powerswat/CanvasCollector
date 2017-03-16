package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by powerswat on 1/27/17.
 */
public class WebTextHandler {
    // Read a string of HTML from the given url
    public String readHTML(String urlStr) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(urlStr);

            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line = "";
            while ((line = br.readLine()) != null)
                sb.append(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // Parse a single string value of json into a structured json format
    public JSONArray parseToJson(String htmlStrFile){
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(htmlStrFile));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return (JSONArray) obj;
    }

    // Remove duplicate data elements from the given Json array based on the given unique key set
    public JSONArray removeDuplicatesInDB(JSONArray jsonArray, String tableName, String key,
                                      DBProcessor dbProcessor){
        JSONArray uniqueJsonArray = new JSONArray();
        SQLProcessor sqlProcessor = new SQLProcessor(new JSONArray(), tableName, key);
        String sql = sqlProcessor.makeSelectQuery(key);
        ArrayList<String> res = dbProcessor.runSelectQuery(sql, key);

        // Generate a hashset to use it as an index map.
        HashSet idMap = new HashSet();
        for (int i = 0; i < res.size(); i++) {
            if (((JSONObject)jsonArray.get(0)).get(key.toLowerCase()) instanceof Long)
                idMap.add(Long.parseLong(res.get(i)));
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            if (!idMap.contains(jsonObject.get(key.toLowerCase())))
                uniqueJsonArray.add(jsonObject);
        }

        return uniqueJsonArray;
    }

    public JSONArray removeDuplicateJson(JSONArray jsonArray) {
        JSONArray resJsonObjs = new JSONArray();
        HashSet<Long> idSet = new HashSet<Long>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            if (idSet.contains(jsonObject.get("id")))
                continue;
            else{
                resJsonObjs.add(jsonObject);
                idSet.add((Long)jsonObject.get("id"));
            }
        }
        return resJsonObjs;
    }
}
