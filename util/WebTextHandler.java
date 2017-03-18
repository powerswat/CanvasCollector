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
import java.util.*;

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
    private boolean isIncorrectTimeOrder(String createdTime, String restTime){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        try {
            Date createdTimestamp = df.parse(createdTime);
            Date restTimestamp = df.parse(restTime);

            if (createdTimestamp.compareTo(restTimestamp) > 0)
                return true;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String[] checkDateTimeFormat(JSONArray jsonArray){
        HashSet<String> res = new HashSet<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            Set keySet = jsonObject.keySet();
            // Iterate over all key-pair set and find all the elements that has a time format
            for (Iterator it = keySet.iterator(); it.hasNext();) {
                String key = (String) it.next();
                if (key.equals("created_at"))
                    continue;

                if (jsonObject.get(key) instanceof String) {
                    String val = (String) jsonObject.get(key);
                    if (val.equals(""))
                        continue;
                    else if (val.replaceAll("[-:TZ0-9]", "").equals(""))
                        res.add(key);
                }
            }
        }
        return res.toArray(new String[res.size()]);
    }

    // Organize time order for the tasks (Fill null time and unreasonable time)
    public JSONArray organizeTimeFormat(JSONArray jsonArray){
        // Check all the time data in the given json array
        String[] restTimeCols = checkDateTimeFormat(jsonArray);

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String createdTime = (String) jsonObject.get("created_at");
            if (createdTime == null)
                continue;

            for (int j = 0; j < restTimeCols.length; j++) {
                String restTime = (String) jsonObject.get(restTimeCols[j]);

                createdTime = createdTime.replaceAll("[TZ]", " ");
                if (restTime == null
                        || isIncorrectTimeOrder(createdTime, restTime.replaceAll("[TZ]", " ")))
                    restTime = genAutomatedTime(createdTime);
                else
                    restTime = restTime.replaceAll("[TZ]", " ");

                jsonObject.put("created_at", createdTime);
                jsonObject.put(restTimeCols[j], restTime);
                jsonArray.set(i, jsonObject);
            }
        }
        return jsonArray;
    }
}
