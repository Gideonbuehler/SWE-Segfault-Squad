package edu.gcc.segfault;

import java.util.ArrayList;

public class Schedule {
    private String semesterName;
    private ArrayList<Course> courses;
    private Calendar calendar;


    public void addCourse(Course toAdd){
        if(checkConflicts(toAdd)) {
            courses.add(toAdd);
        }
    }

    public void removeCourse(Course toRemove){

    }

    /**
     *
     * @param toCheck - the class that we're trying to add to the schedule
     * @return - true if there is no conflict with the current courses in schedule
     * false if there is a conflict with one of the classes
     */
    public boolean checkConflicts(Course toCheck){
        for(Course c: courses){
            for(String d: c.getDays()){
                for(String d2: toCheck.getDays()){
                    if(d.equals(d2)){
                        if(c.getStartTime().isBefore(toCheck.getStartTime()) && c.getEndTime().isAfter(toCheck.getStartTime())) {
                            return false;
                        }
                        if(c.getStartTime().equals(toCheck.getStartTime()) || c.getEndTime().equals(toCheck.getStartTime())){
                            return false;
                        }

                    }
                }
            }
        }


        // ay yi yi
        return true;
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
