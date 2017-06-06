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
    private static IndivScheduler ids;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception{
        ssd = new StudyScheduleDriver();
        ssd.prepareData();
    }

    @Test
    public void getInstance() throws Exception {
        ids = IndivScheduler.getInstance(ssd.getCnfgHndlr(), ssd.getDbProcessor(),
                ssd.getColNames(), ssd.getTypes());
        assertNotNull(ids);
    }

    public int countNotValidAssignments(ArrayList<Assignment> assignments,
                                        ArrayList<Integer> courseIDs){
        int notValidCnt = 0;
        for (Assignment assignment : assignments) {
            boolean isValid = false;
            for (Integer courseID : courseIDs) {
                if (courseID == assignment.getCourseID()) {
                    isValid = true;
                    break;
                }
                if (isValid)
                    break;
            }
            if (!isValid)
                notValidCnt++;
        }

        return notValidCnt;
    }

    // Check whether the number of hourly tasks is correct
    public void testNumHourlyTasks(){

    }

    @Test
    public void generateScheduleTable() throws Exception {
        // Sample from the original data
        // Get sample courseIDs
        ArrayList<Integer> sampleCourses = new ArrayList<>();
        Student sampleStudent = ssd.getStudents()[0];
        for (int i = 0; i < 2; i++)
            sampleCourses.add(sampleStudent.getCourseIDs().get(i));
        sampleStudent.setCourseIDs(sampleCourses);

        // Get assignment data from each sampled course
        ArrayList<Assignment> assignments = new ArrayList<>();
        ArrayList<Assignment> oldAssignments = sampleStudent.getAssignments();

        ArrayList<Integer> courseIDsOfStudent = sampleStudent.getCourseIDs();

        for (int i = 0; i < oldAssignments.size(); i++) {
            Assignment assignment = oldAssignments.get(i);

            for (int j = 0; j < courseIDsOfStudent.size(); j++)
                if (assignment.getCourseID() == courseIDsOfStudent.get(j))
                    assignments.add(assignment);
        }

        // Check whether the sampled assignments only from the sampled courses.
        assertEquals(0,
                countNotValidAssignments(assignments, sampleStudent.getCourseIDs()));

        // Provide the sample student data
        Student[] students = {sampleStudent};
        ids.setStudents(students);

        ids.generateScheduleTable();

        // Check whether the number of hourly tasks is correct
        testNumHourlyTasks();

        System.out.println();
    }

    @Test
    public void insertIntoTable() throws Exception {
    }
}