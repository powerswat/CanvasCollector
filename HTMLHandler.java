import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by powerswat on 1/27/17.
 */
public class HTMLHandler {
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

//    public String parseHTML(){}

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
}