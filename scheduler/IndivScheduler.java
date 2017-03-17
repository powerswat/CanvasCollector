package scheduler;

import configure.ConfigHandler;
import util.DBProcessor;

/**
 * Created by powerswat on 3/8/17.
 */
public class IndivScheduler {
    private static IndivScheduler instance = null;

    private static ConfigHandler cnfgHndlr;
    private static DBProcessor dbProcessor;

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

    public static void runScheduler(){

    }
}
