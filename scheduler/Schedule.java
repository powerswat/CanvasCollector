package scheduler;

import org.joda.time.DateTime;

/**
 * Created by powerswat on 4/20/17.
 */
public class Schedule {
    private int studentID;
    private int assignmentID;
    private int courseID;
    private DateTime startTime;
    private DateTime endTime;
    private float pointPerDay;
    private String name;

    public Schedule(int studentID, int assignmentID, int courseID,
                    DateTime startTime, DateTime endTime, float pointPerDay, String name){
        this.studentID = studentID;
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pointPerDay = pointPerDay;
        this.name = name;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public int getStudentID() {
        return studentID;
    }

    public int getAssignmentID() {
        return assignmentID;
    }

    public int getCourseID() {
        return courseID;
    }

    public float getPointPerDay() {
        return pointPerDay;
    }

    public String getName() {
        return name;
    }
}
