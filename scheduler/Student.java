package scheduler;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by powerswat on 3/8/17.
 */
public class Student {
    private int id;
    private ArrayList<Integer> courseIDs;
    private ArrayList<Assignment> assignments;
    private HashSet<Integer> courseIDSet;

    public Student(int id){
        this.id = id;
        courseIDs = new ArrayList<>();
        assignments = new ArrayList<>();
        courseIDSet = new HashSet<>();
    }

    public void addCourseID(Integer courseID){
        if (!courseIDSet.contains(courseID)) {
            courseIDs.add(courseID);
            courseIDSet.add(courseID);
        }
    }

    public void addAssignment(Assignment assignment){
        assignments.add(assignment);
    }
}
