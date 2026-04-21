package edu.gcc.segfault;

import java.util.ArrayList;
import java.util.Iterator;


public class Profile {
    //Force them to update year as a dropdown?
    //change this to int for graduation year?
    private String year;
    //Standardized in capital letters
    private String major;
    //minors are standardized in capital letters to make it easier to check them
    private ArrayList<String> minors;
    //Only allowing the user to prioritize one minor
    private String minor;
    private ArrayList<Course> completedCourses;
    private ArrayList<String> comp;
    private String omg;


    public Profile(String schoolYear, String major, ArrayList<String> minors, ArrayList<Course> courses){
        year = schoolYear.toUpperCase();
        this.major = major.toUpperCase();
        this.minors = new ArrayList<>();
        for(String m : minors){
            this.minors.add(m.toUpperCase());
        }
        minor = "BUSINESS";
        completedCourses = courses;
        comp = new ArrayList<>();
        comp.add("No completed courses yet");
        omg = "No completed courses yet";
    }

    /**
     * Backend for the user to change their college year
     * Note: Should implement dropdown to pick their year,
     * otherwise will need to add more checks to make sure they pick a valid year.
     * @param year - String of their college year (freshman, sophomore, junior, senior, super senior)
     * @return true if the year updated.
     */
    public boolean updateYear(String year) {
        year = year.toUpperCase();
        if(year.equals("FRESHMAN") || year.equals("SOPHOMORE") || year.equals("JUNIOR") || year.equals("SENIOR") || year.equals("SUPER SENIOR")) {
            this.year = year;
        }
        return this.year.equals(year);
    }

    /**
     * Backend for the user to change their major
     * @param major - String of their update college major
     * @return - true if the major updated
     */
    public boolean updateMajor(String major) {
        for(int j = 0; j < major.length(); j++){
            if(Character.isDigit(major.charAt(j))){
                return false;
            }
        }
        this.major = major.toUpperCase();

        return this.major.equals(major.toUpperCase());
    }

    /**
     * Adds the minor to the list of minors in the profile.
     * @param minor - the minor to be added to the list
     * @return true if the minor was added to the list. False if the minor was not added or the
     * list already contained that minor
     */
    public boolean addMinor(String minor){
        for(int j = 0; j < minor.length(); j++){
            if(Character.isDigit(minor.charAt(j))){
                return false;
            }
        }
        minor = minor.toUpperCase();
        if(!minors.contains(minor)){
            minors.add(minor);
            return true;
        }
        //check whether or not it just failed vs already was in the list of minors?
        return false;
    }
    public boolean deleteMinor(String minor){
        minor = minor.toUpperCase();

        if(minors.contains(minor)){
            minors.remove(minor);

            return true;
        }
        return false;
    }

    public boolean updateMinor(String m){
        for(int j = 0; j < m.length(); j++){
            if(Character.isDigit(m.charAt(j))){
                return false;
            }
        }
        m = m.toUpperCase();
        this.minor = m;
        return true;
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
    public boolean setCompletedCourses(ArrayList<Course> completed){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < completed.size() - 1; i++){
            String temp = completed.get(i).getCourseCode();
            str.append(temp, 0, temp.length() - 2).append(", ");
        }
        str.append(completed.getLast().getCourseCode().substring(0, completed.getLast().getCourseCode().length() - 2));
        omg = str.toString();


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

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getOmg() {
        return omg;
    }
}

