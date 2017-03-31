package scheduler;

import configure.ConfigHandler;
import factories.SchedulerFactory;
import util.DBProcessor;
import org.joda.time.DateTime;
import util.SQLProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by powerswat on 3/8/17.
 */
public class IndivScheduler implements SchedulerFactory{
    private static IndivScheduler instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;

    private static String tableName = "SCHEDULES";
    private static String pkCol = "ID";

    public static IndivScheduler getInstance(ConfigHandler cnfgHndlr, DBProcessor dbProcessor){
        if (instance == null){
            synchronized (IndivScheduler.class){
                if (instance == null)
                    instance = new IndivScheduler(cnfgHndlr, dbProcessor);
            }
        }
        return instance;
    }

    public IndivScheduler(ConfigHandler cnfgHndlr, DBProcessor dbProcessor){
        this.cnfgHndlr = cnfgHndlr;
        this.dbProcessor = dbProcessor;
    }

    public void runScheduler(ArrayList<String[]> sqlData){
        // Plan based on constraints
        planSchedule(sqlData);

        System.out.println();
    }


    @Override
    public void planSchedule(ArrayList<String[]> sqlData) {


        System.out.println();
    }

    @Override
    public void insertTable() {

    }
}
