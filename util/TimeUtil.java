package util;

import org.joda.time.DateTime;
import scheduler.Assignment;
import scheduler.Schedule;
import scheduler.Student;

import java.util.ArrayList;

/**
 * Created by powerswat on 4/23/17.
 */
public class TimeUtil {
    public DateTime findEarliestAvailability(Student student, Assignment assignment){
        DateTime earliestTime = assignment.getCreatedAt().plusDays(1).minuteOfDay().setCopy(0);
        int[] dayPreference = student.getWorkDayPreference();
        int[] hourPreference = student.getWorkHourPreference();

        // Iterate until it finds the preferred day
        for (int i = 1; i < dayPreference.length; i++) {
            int dayOfWeek = earliestTime.getDayOfWeek();
            if (dayPreference[dayOfWeek] == 1)
                break;
            earliestTime = earliestTime.plusDays(1);
        }

        // Iterate until it finds the preferred time
        for (int i = 0; i < hourPreference.length; i++) {
            earliestTime = earliestTime.plusHours(1);
            int hourOfDay = earliestTime.getHourOfDay();
            if (hourPreference[hourOfDay] == 1)
                return earliestTime;
        }

        return null;
    }

    // Generate an available schedule based on the user's preference
    public Schedule generateSchedule(Student student, Assignment assignment, DateTime availability){
        DateTime startTime = availability;
        DateTime endTime = availability.plusHours((int)Math.ceil(assignment.getHoursPerDay()));
        return new Schedule(student.getId(), assignment.getId(), startTime, endTime,
                assignment.getPointsPossible() / assignment.getNumDays());
    }

    // Check the eligibility of the given schedule
    public boolean isEligible(Student student, Schedule schedule){
        // Check whether there is no overlapped schedule
        if(!isOverlapped(student.getSchedules(), schedule.getStartTime(), schedule.getEndTime()))
            return true;

        return false;
    }

    public boolean isOverlapped(ArrayList<Schedule> schedules,
                                DateTime startTime, DateTime endTime){
        for (int i = 0; i < schedules.size(); i++) {
            Schedule curSchedule = schedules.get(i);
            if (!(startTime.isAfter(curSchedule.getEndTime()) ||
                    endTime.isBefore(curSchedule.getStartTime())))
                return true;
        }
        return false;
    }
}
