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
    private ArrayList<Schedule> schedules;
    private HashSet<Integer> courseIDSet;
    private int[] workDayPreference = {-1,1,1,1,1,1,0,0};
    private int[] workHourPreference = {-1,-1,-1,-1,-1,-1,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,-1};

    public Student(int id){
        this.id = id;
        courseIDs = new ArrayList<>();
        assignments = new ArrayList<>();
        schedules = new ArrayList<>();
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

    public void addSchedule(Schedule schedule){
        schedules.add(schedule);
    }

    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }

    public int getId() {
        return id;
    }

    public int[] getWorkDayPreference() {
        return workDayPreference;
    }

    public int[] getWorkHourPreference() {
        return workHourPreference;
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }
}
