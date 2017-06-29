package viz_generator;

import java.util.ArrayList;

/**
 * Created by powerswat on 6/8/17.
 */
public class VizFormat {
    String date;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> startTimes = new ArrayList<>();
    int numTasks;

    public VizFormat(String date){
        this.date = date;
    }

    public void addName(String name){
        names.add(name);
    }

    public void addNumTasks(){
        numTasks++;
    }

    public void addStartTime(String time){
        startTimes.add(time);
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public ArrayList<String> getStartTimes() {
        return startTimes;
    }

    public int getNumTasks() {
        return numTasks;
    }
}
