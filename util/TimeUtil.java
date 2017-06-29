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
    private static int[] dayPreference;
    private static int[] hourPreference;

    public Schedule findEarliestAvailability(Student student, Assignment assignment, int kDays){
        DateTime earliestTime
                = assignment.getCreatedAt().plusDays(1+kDays).minuteOfDay().setCopy(0);
        dayPreference = student.getWorkDayPreference();
        hourPreference = student.getWorkHourPreference();

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
                break;
        }

        // Generate an available schedule based on the user's preference
        Schedule schedule = generateSchedule(student, assignment, earliestTime);
        while (!isEligible(student, schedule)
                && schedule.getEndTime().isBefore(assignment.getDueAt())) {
            earliestTime = earliestTime.plusHours(1);
            schedule = generateSchedule(student, assignment, earliestTime);
        }
        if (!schedule.getEndTime().isBefore(assignment.getDueAt()))
            return null;
        return schedule;

    }

    // Generate an available schedule based on the user's preference
    public Schedule generateSchedule(Student student, Assignment assignment, DateTime availability){
        DateTime startTime = availability;
        DateTime endTime = availability.plusHours((int)Math.ceil(assignment.getHoursPerDay()));
        return new Schedule(student.getId(), assignment.getId(), assignment.getCourseID(),
                startTime, endTime,
                assignment.getPointsPossible() / assignment.getNumDays(), assignment.getName());
    }

    // Check the selected time is in the current user's preferred time
    public boolean isInPreference(DateTime startTime){
        int startHour = startTime.getHourOfDay();
        int dayNum = startTime.getDayOfWeek();

        if (dayPreference[dayNum] < 0 || hourPreference[startHour] < 0)
            return false;

        return true;
    }

    // Check the eligibility of the given schedule
    public boolean isEligible(Student student, Schedule schedule){
        // Check whether there is no overlapped schedule
        if(!isOverlapped(student.getSchedules(), schedule.getStartTime(), schedule.getEndTime())
                && isInPreference(schedule.getStartTime()))
            return true;

        return false;
    }

    // Check whether there is no overlapped schedule
    public boolean isOverlapped(ArrayList<Schedule> schedules,
                                DateTime startTime, DateTime endTime){
        for (int i = 0; i < schedules.size(); i++) {
            Schedule curSchedule = schedules.get(i);
            if (curSchedule == null)
                continue;
            if (!(startTime.isAfter(curSchedule.getEndTime().minusMinutes(1)) ||
                    endTime.isBefore(curSchedule.getStartTime().plusMinutes(1))))
                return true;
        }
        return false;
    }
}
