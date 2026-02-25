package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;

public class Schedule {
    private String semesterName;
    private ArrayList<Course> courses;
    private Calendar calendar;

    public Schedule(String name){
        semesterName = name;
        courses = new ArrayList<>();
        calendar = new Calendar();
    }
    public void addCourse(Course toAdd){
        courses.add(toAdd);
    }

    public void removeCourse(Course toRemove){

    }

    public boolean checkConflicts(Course toCheck){
        for(Course c: courses){
            for(String d: c.getDays()){
                for(String d2: toCheck.getDays()){
                    System.out.println(d + " " + d2);
                    if(d.equals(d2)){
                        //if the start time is in the time of the other classes
                        if(!c.getStartTime().isAfter(toCheck.getStartTime()) && c.getEndTime().isAfter(toCheck.getStartTime())){
                            return false;
                        }
                        //if the end time is in the time of another class
                        if(c.getStartTime().isBefore(toCheck.getEndTime()) && c.getEndTime().isAfter(toCheck.getEndTime())){
                            return false;
                        }
                        //check if there is overlap on the specific end/beginning times
                        if(c.getStartTime().equals(toCheck.getStartTime()) || c.getEndTime().equals(toCheck.getStartTime()) ||
                        c.getEndTime().equals(toCheck.getEndTime()) || c.getStartTime().equals(toCheck.getEndTime())){
                            return false;
                        }
                    }
                }
            }
        }

        //this is my 5th test

        return true;
    }

    public static void main(String[] args) {
        Schedule s = new Schedule("F25");
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");
        ArrayList<String> days2 = new ArrayList<>();
        days2.add("Wednesday");
        ArrayList<String> days3 = new ArrayList<>();
        days3.add("Tuesday");
        s.addCourse(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days , 3, true, false, 20, 30));
        System.out.println(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days2, 1, true, false, 20, 30)));
        System.out.println(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days3, 1, true, false, 20, 30)));

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
