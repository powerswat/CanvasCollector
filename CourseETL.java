/**
 * Created by youngsukcho on 2017. 1. 26..
 */
public class CourseETL implements CanvasETLFact {
    private static CourseETL instance = null;
    private String jsonStr = "";
    private String coursePath = "courses";

    public static CourseETL getInstance() {
        if (instance == null)
            synchronized (CourseETL.class) {
                if (instance == null)
                    instance = new CourseETL();
            }
        return instance;
    }

    // Start collecting course information from the given token
    public void runProcess(){

    }

    @Override
    public void readAPI(String addr) {

    }

    @Override
    public void tabulate() {

    }

    @Override
    public void insertToDB() {

    }
}
