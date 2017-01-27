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

    // Read the config file and parse it
    private static void loadConfig(String configPath){
        Properties pFile = new Properties();
        try {
            InputStream input = new FileInputStream(configPath);
            pFile.load(input);
            webAddr = pFile.getProperty("webAddr");
            token = pFile.getProperty("token");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Entry point to the system
    public static void main(String[] args){
        // Read the config file and parse it
        loadConfig("./config/apiconfig.properties");

        // Start collecting course information from the given token
        CourseETL cEtl = CourseETL.getInstance();
        cEtl.runProcess();
    }
}
