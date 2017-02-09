/**
 * Created by youngsukcho on 2017. 1. 24..
 */
public class CollectorDriver {
    private static DBProcessor dbProcessor = new DBProcessor();

    private static String webAddr;
    private static String token;
    private static String apiPath = "api/v1/";

    private static CourseETL cEtl;
    private static UserETL uETL;

    // Entry point to the system
    public static void main(String[] args){
        // Read the config file and parse it
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        dbProcessor.connectToDB(cnfgHndlr);

        // Start collecting course information from the given token
        cEtl = CourseETL.getInstance(cnfgHndlr, dbProcessor);
        cEtl.runProcess(cnfgHndlr.getWebAddr() + apiPath,
                            cnfgHndlr.getToken());

        // Start collecting user information from the given token
        uETL = uETL.getInstance(cnfgHndlr, dbProcessor);
        uETL.runProcess(cnfgHndlr.getWebAddr() + apiPath,
                cnfgHndlr.getToken());
    }
}
