package viz_generator;

import configure.ConfigHandler;
import util.DBProcessor;
import util.DataUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by powerswat on 6/8/17.
 */
public class VizCSVDriver {
    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor = new DBProcessor();

    // Read Schedule table
    public static ArrayList<ArrayList<String>> readScheduleTable(){
        // Read the config file and parse it
        cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);

        String sql = "SELECT * FROM SCHEDULES;";
        String[] items = {"POINTS_POSSIBLE", "ID", "USER_ID", "E_COURSE_ID",
                        "A_CREATED_AT", "A_NAME", "A_ID", "DUE_AT"};

        return dbProcessor.runSelectQuery(sql, items);
    }

    // Generate a file saving format for the visualization
    public static ArrayList<VizFormat> generateVizSaveFormat(
            ArrayList<ArrayList<String>> sqlData){
        Hashtable<String, Integer> dateToIdxTbl = new Hashtable<>();
        ArrayList<VizFormat> vizData = new ArrayList<>();
        int idx = 0;
        for (ArrayList<String> row : sqlData) {
            String date = row.get(4).split(" ")[0];
            if (!dateToIdxTbl.containsKey(date)){
                VizFormat vizFormat = new VizFormat(date);
                vizFormat.addName(row.get(5));
                vizFormat.addStartTime(row.get(4).split(" ")[1]);
                vizFormat.addNumTasks();
                vizData.add(vizFormat);
                dateToIdxTbl.put(date, idx++);
            } else {
                VizFormat vizFormat = vizData.get(dateToIdxTbl.get(date));
                vizFormat.addName(row.get(5));
                vizFormat.addStartTime(row.get(4).split(" ")[1]);
                vizFormat.addNumTasks();
                vizData.set(dateToIdxTbl.get(date), vizFormat);
            }
        }
        return vizData;
    }

    public static void main(String[] args){
        // Read Schedule table
        ArrayList<ArrayList<String>> sqlData = readScheduleTable();

        // Generate a file saving format for the visualization
        ArrayList<VizFormat> vizData = generateVizSaveFormat(sqlData);

        // Save the generated visualization format.
        DataUtil dataUtil = new DataUtil();
        File file = new File(".");
        String baseDir = file.getAbsolutePath();
        String dataDir = "data/";
        dataUtil.saveData(vizData, baseDir + dataDir + "schedViz.tsv");
    }
}
