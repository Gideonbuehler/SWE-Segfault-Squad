package edu.gcc.segfault;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Profile {
    private String year;
    private String major;
    private ArrayList<String> minors;
    private ArrayList<Course> completedCourses;

    public Profile(String schoolYear, String major, ArrayList<String> minors, ArrayList<Course> courses){
        year = schoolYear;
        this.major = major;
        this.minors = new ArrayList<>(minors);
        completedCourses = courses;
    }

    public boolean updateYear(String year) {
        return false;
    }

    public boolean updateMajor(String major) {
        return false;
    }

    public boolean updateMinors(ArrayList<String> minors) {
        return false;
    }

    public boolean updateCompletedCourses(ArrayList<Course> completedCourses) {
        return false;
    }

    //Getters
    public String getYear() {
        return year;
    }

    public String getMajor() {
        return major;
    }

    public ArrayList<String> getMinors() {
        return minors;
    }

    public ArrayList<Course> getCompletedCourses() {
        return completedCourses;
    }

}

