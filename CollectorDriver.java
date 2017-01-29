import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by youngsukcho on 2017. 1. 24..
 */
public class CollectorDriver {

    private static String webAddr;
    private static String token;
    private static String apiPath = "api/v1/";

    // Entry point to the system
    public static void main(String[] args){
        // Read the config file and parse it
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");

        // Start collecting course information from the given token
        CourseETL cEtl = CourseETL.getInstance(cnfgHndlr);
        cEtl.runProcess(cnfgHndlr.getWebAddr() + apiPath,
                            cnfgHndlr.getToken());
    }
}
