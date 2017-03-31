package scheduler;

/**
 * Created by powerswat on 3/8/17.
 */
public class Student {
    private int id;
    private String name;
    private Course[] courses;
    private Task[] tasks;

    public Student(int id){
        this.id = id;
    }

    public void setCourses(Course[] courses) {
        this.courses = courses;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }
}
