package scheduler;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by powerswat on 3/8/17.
 */
public class Student {
    private int id;
    private ArrayList<Integer> courseIDs;
    private ArrayList<Integer> taskIDs;
    private HashSet<Integer> courseIDSet;

    public Student(int id){
        this.id = id;
        courseIDs = new ArrayList<>();
        taskIDs = new ArrayList<>();
        courseIDSet = new HashSet<>();
    }

    public void addCourseID(Integer courseID){
        if (!courseIDSet.contains(courseID)) {
            courseIDs.add(courseID);
            courseIDSet.add(courseID);
        }
    }

    public void addTaskID(Integer taskID){
        taskIDs.add(taskID);
    }
}
