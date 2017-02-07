import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by powerswat on 1/28/17.
 */
public class CourseETLTest {
    @Test
    public void getInstance() throws Exception {
        ConfigHandler cnfgHndlr = ConfigHandler.getInstance("./config/apiconfig.properties");
        DBProcessor dbProcessor = new DBProcessor();
        dbProcessor.connectToDB(cnfgHndlr);
        assertNotEquals(null, CourseETL.getInstance(cnfgHndlr, dbProcessor));
    }

    @Test
    public void runProcess() throws Exception {

    }

    @Test
    public void readAPI() throws Exception {

    }

    @Test
    public void tabulate() throws Exception {

    }

    @Test
    public void insertToDB() throws Exception {

    }

}