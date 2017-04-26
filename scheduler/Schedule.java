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

    public Schedule(int studentID, int assignmentID, int courseID,
                    DateTime startTime, DateTime endTime, float pointPerDay){
        this.studentID = studentID;
        this.assignmentID = assignmentID;
        this.courseID = courseID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pointPerDay = pointPerDay;
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
}
