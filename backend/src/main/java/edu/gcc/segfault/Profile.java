package edu.gcc.segfault;

import java.util.ArrayList;


public class Profile {
    //Force them to update year as a dropdown?
    //change this to int for graduation year?
    private String year;
    //Standardized in capital letters
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

    /**
     * Backend for the user to change their college year
     * @param year - String of their college year (freshman, sophomore, junior, senior, super senior)
     * @return true if the year updated.
     */
    public boolean updateYear(String year) {
        this.year = year;
        return this.year.equals(year);
    }

    /**
     * Backend for the user to change their major
     * @param major - String of their update college major
     * @return - true if the major updated
     */
    public boolean updateMajor(String major) {
        this.major = major.toUpperCase();
        return this.major.equals(major.toUpperCase());
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

    public boolean addCompletedCourses(ArrayList<Course> completedCourses) {
        for(Course c : completedCourses){
            if(!this.completedCourses.contains(c)){
                this.completedCourses.add(c);
            }
        }
        //perform check to make sure that all the courses were added?
        return true;
    }
    public boolean removeCompletedCourses(ArrayList<Course> cc){
        for(Course c : cc){
            if(completedCourses.contains(c)){
                completedCourses.remove(c);
            }
        }
        return true;
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

