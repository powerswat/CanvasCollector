package scheduler;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by powerswat on 3/8/17.
 */
public class Assignment {
    private int id;
    private int courseID;
    private int priority;
    private DateTime createdAt;
    private DateTime dueAt;
    private int pointsPossible;
    private int numDays;
    private float hoursPerDay;

    private static DateTimeFormatter timeFormatter
            = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");

    public Assignment(int id, int courseID, String name, String createdAt, String dueAt, int pointsPossible){
        this.id = id;
        this.courseID = courseID;
        String name1 = name;
        this.createdAt = timeFormatter.parseDateTime(createdAt);
        this.dueAt = timeFormatter.parseDateTime(dueAt);
        this.pointsPossible = pointsPossible;
        this.numDays = this.dueAt.getDayOfYear() - this.createdAt.getDayOfYear() - 1;
//        this.hoursPerDay = (float) pointsPossible / (float) numDays;

        // Use the following default setting tentatively
        this.hoursPerDay = 1;
    }

    public float getHoursPerDay() {
        return hoursPerDay;
    }

    public int getId() {
        return id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public int getPointsPossible() {
        return pointsPossible;
    }

    public int getNumDays() {
        return numDays;
    }

    public DateTime getDueAt() {
        return dueAt;
    }

    public int getCourseID() {
        return courseID;
    }
}
