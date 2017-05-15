package scheduler;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by powerswat on 5/4/17.
 */
public class IndivSchedulerTest {
    private static StudyScheduleDriver ssd;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        ssd = new StudyScheduleDriver();
        ssd.prepareData();
    }

    @Test
    public void getInstance() throws Exception {
    }

    @Test
    public void generateScheduleTable() throws Exception {
        // Sample from the original data\
        // Get sample courseIDs
        ArrayList<Integer> sampleCourses = new ArrayList<>();
        Student sampleStudent = ssd.getStudents()[0];
        for (int i = 0; i < 2; i++)
            sampleCourses.add(sampleStudent.getCourseIDs().get(i));
        sampleStudent.setCourseIDs(sampleCourses);

        System.out.println();
    }

    @Test
    public void insertIntoTable() throws Exception {
    }
}