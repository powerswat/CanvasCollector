package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    // Fill due time with created time if it is null.
    public String genAutomatedTime(String timeStr){
        String newTimeStr = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

        try {
            Date date = df.parse(timeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 7);
            newTimeStr = df.format(calendar.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return newTimeStr;
    }

    // Pull due time back to a week after created time if it is ahead of the created time.
    public boolean isIncorrectTimeOrder(String createdTime, String dueTime){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        try {
            Date createdTimestamp = df.parse(createdTime);
            Date dueTimestamp = df.parse(dueTime);

            if (createdTimestamp.compareTo(dueTimestamp) > 0)
                return true;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Organize time order for the tasks (Fill null time and unreasonable time)
    public JSONArray organizeTimeFormat(JSONArray jsonArray){
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String createdTime = (String) jsonObject.get("created_at");
            String dueTime = (String) jsonObject.get("due_at");

            if (createdTime == null)
                continue;

            createdTime = createdTime.replaceAll("[TZ]", " ");
            if (dueTime == null
                    || isIncorrectTimeOrder(createdTime, dueTime.replaceAll("[TZ]", " ")))
                dueTime = genAutomatedTime(createdTime);
            else
                dueTime = dueTime.replaceAll("[TZ]", " ");

            jsonObject.put("created_at", createdTime);
            jsonObject.put("due_at", dueTime);
            jsonArray.set(i, jsonObject);
        }
        return jsonArray;
    }
}
