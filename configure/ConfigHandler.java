package configure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by powerswat on 1/28/17.
 */
public class ConfigHandler {

    private static ConfigHandler instance = null;

    private static String webAddr;
    private static String token;
    private static String dbAddr;
    private static String dbAcct;
    private static String dbPass;
    private static String dbName;

    public static ConfigHandler getInstance(String configPath) {
        if (instance == null)
            synchronized (ConfigHandler.class){
                if (instance == null)
                    instance = new ConfigHandler(configPath);
            }
        return instance;
    }

    private ConfigHandler(String configPath){
        loadConfig(configPath);
    }

    // Read the config file and parse it
    private void loadConfig(String configPath){
        Properties pFile = new Properties();
        try {
            InputStream input = new FileInputStream(configPath);
            pFile.load(input);
            webAddr = pFile.getProperty("webAddr");
            token = pFile.getProperty("token");
            dbAddr = pFile.getProperty("dbAddr");
            dbAcct = pFile.getProperty("dbAcct");
            dbPass = pFile.getProperty("dbPass");
            dbName = pFile.getProperty("dbName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getWebAddr() {
        return webAddr;
    }

    public static String getToken() {
        return token;
    }

    public static String getDbAddr() { return dbAddr; }

    public static String getDbAcct() {
        return dbAcct;
    }

    public static String getDbPass() {
        return dbPass;
    }

    public static String getDbName() {
        return dbName;
    }
}
