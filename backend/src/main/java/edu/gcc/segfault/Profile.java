package edu.gcc.segfault;

import java.util.ArrayList;


public class Profile {
    //Force them to update year as a dropdown?
    private String year;
    private String major;
    //minors are standardized in capital letters to make it easier to check them
    private ArrayList<String> minors;
    private ArrayList<Course> completedCourses;


    public Profile(String schoolYear, String major, ArrayList<String> minors, ArrayList<Course> courses){
        year = schoolYear;
        this.major = major;
        this.minors = new ArrayList<>(minors);
        completedCourses = courses;
    }

    public boolean updateYear(String year) {
        this.year = year;
        if(this.year.equals(year)) {
            return true;
        }
        return false;
    }

    public boolean updateMajor(String major) {
        return false;
    }

    /**
     * Adds the minor to the list of minors in the profile.
     * @param minor - the minor to be added to the list
     * @return true if the minor was added to the list. False if the minor was not added or the
     * list already contained that minor
     */
    public boolean addMinors(String minor){
        minor = minor.toUpperCase();
        if(!minors.contains(minor)){
            minors.add(minor);
            return true;
        }
        //check whether or not it just failed vs already was in the list of minors?
        return false;
    }
    public boolean removeMinor(String minor){
        minor = minor.toUpperCase();
        if(minors.contains(minor)){
            minors.remove(minor);
            return true;
        }
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

