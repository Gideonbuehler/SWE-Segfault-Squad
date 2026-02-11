package edu.gcc.segfault;

import java.util.ArrayList;


public class Profile {
    private String year;
    private String major;
    private ArrayList<String> minors;
    private ArrayList<Course> completedCourses;

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

