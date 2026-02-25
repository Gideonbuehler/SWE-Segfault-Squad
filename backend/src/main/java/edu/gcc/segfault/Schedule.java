package edu.gcc.segfault;

import java.util.ArrayList;

public class Schedule {
    private String semesterName;
    private ArrayList<Course> courses;
    private Calendar calendar;


    public void addCourse(Course toAdd){

    }

    public void removeCourse(Course toRemove){

    }

    public boolean checkConflicts(Course toCheck){
        for(Course c: courses){
            for(String d: c.getDays()){
                for(String d2: toCheck.getDays()){
                    if(d.equals(d2)){
                        if(!c.getStartTime().isAfter(toCheck.getStartTime()) && c.getEndTime().isAfter(toCheck.getStartTime())){

                        }
                    }
                }
            }
        }



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
