package edu.gcc.segfault;

import java.util.ArrayList;

public class Schedule {
    private String semesterName;
    private ArrayList<Course> courses;
    private Calendar calendar;


    public void addCourse(Course toAdd){
        if (toAdd == null) return;
        courses.remove(toAdd);
    }

    public void removeCourse(Course toRemove){
        if (toRemove == null) return;
        courses.remove(toRemove);
    }

    public boolean checkConflicts(Course toCheck){
        return false;
    }

    public boolean saveSchedule(){
        return false;
    }

    public boolean loadSchedule(){
        return false;
    }

    public ArrayList<Course> getCourses(){
        return new ArrayList<>();
    }
}
