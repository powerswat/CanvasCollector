package scheduler;

import configure.ConfigHandler;
import util.DBProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by powerswat on 3/8/17.
 */
public class StudyScheduleDriver {
    private static DBProcessor dbProcessor = new DBProcessor();
    private static IndivScheduler indivScheduler;
    private static TeamScheduler teamScheduler;

    public static void main(String[] args){
        // Read schedule data for every individual student

        // Read the config file and parse it
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Connected to the database
        dbProcessor.connectToDB(cnfgHndlr);

        // Schedule individual tasks
        indivScheduler = IndivScheduler.getInstance(cnfgHndlr, dbProcessor);
        indivScheduler.runScheduler();

        // Schedule individual tasks

        System.out.println();
    }
}
