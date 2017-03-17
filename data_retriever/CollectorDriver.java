package data_retriever;

import configure.ConfigHandler;
import util.DBProcessor;

/**
 * Created by youngsukcho on 2017. 1. 24..
 */
// TODO: Check visualization at http://bl.ocks.org/KathyZ/c2d4694c953419e0509b
public class CollectorDriver {
    private static DBProcessor dbProcessor = new DBProcessor();

    private static String webAddr;
    private static String token;
    private static String apiPath = "api/v1/";

    private static CourseETL cEtl;
    private static UserETL uETL;
    private static TaskETL tETL;

    // Entry point to the system
    public static void main(String[] args){
        // Read the config file and parse it
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);

        // Start collecting course information from the given token
        cEtl = CourseETL.getInstance(cnfgHndlr, dbProcessor);
        cEtl.runProcess(cnfgHndlr.getWebAddr() + apiPath,
                cnfgHndlr.getToken());

        // Start collecting user information from the given token
        uETL = uETL.getInstance(cnfgHndlr, dbProcessor);
        uETL.runProcess(cnfgHndlr.getWebAddr() + apiPath,
                cnfgHndlr.getToken());

        // Start collecting assignment information from the given token
        tETL = tETL.getInstance(cnfgHndlr, dbProcessor);
        tETL.runProcess(cnfgHndlr.getWebAddr() + apiPath,
                cnfgHndlr.getToken());
    }
}
