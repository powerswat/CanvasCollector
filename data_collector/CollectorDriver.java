package data_collector;

import configure.ConfigHandler;
import util.DBProcessor;

/**
 * Created by youngsukcho on 2017. 1. 24..
 */
public class CollectorDriver {
    private static DBProcessor dbProcessor = new DBProcessor();

    private static String webAddr;
    private static String token;

    private static UserETL uETL;
    private static TaskETL tETL;
    private static EnrollmentETL eETL;

    // Entry point to the system
    public static void main(String[] args){
        // Read the config file and parse it
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);

        // Start collecting course information from the given token
        CourseETL cEtl = CourseETL.getInstance(cnfgHndlr, dbProcessor);
        String apiPath = "api/v1/";
        cEtl.runProcess(cnfgHndlr.getWebAddr() + apiPath, cnfgHndlr.getToken());

        // Start collecting user information from the given token
        uETL = uETL.getInstance(cnfgHndlr, dbProcessor);
        uETL.runProcess(cnfgHndlr.getWebAddr() + apiPath, cnfgHndlr.getToken());

        // Start collecting assignment information from the given token
        tETL = tETL.getInstance(cnfgHndlr, dbProcessor);
        tETL.runProcess(cnfgHndlr.getWebAddr() + apiPath, cnfgHndlr.getToken());

        // Start collecting enrollment information from the given token
        eETL = eETL.getInstance(cnfgHndlr, dbProcessor);
        eETL.runProcess(cnfgHndlr.getWebAddr() + apiPath, cnfgHndlr.getToken());
    }
}
