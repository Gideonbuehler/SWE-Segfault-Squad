package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;

public class Course {
    private String courseCode;
    private String courseName;
    private String location;
    private String professor;
    private String department;
    private LocalTime startTime;
    private LocalTime endTime;
    private ArrayList<String> days;
    private int credits;
    private String semester;
    private Boolean isOpen;
    private Boolean isLab;
    private int openSeats;
    private int totalSeats;

    // Constructor for main
    Course(String courseCode, String courseName, String professor, String department,
           String location, String semester, LocalTime startTime, LocalTime endTime,
           ArrayList<String> days, int credits, boolean isOpen, boolean isLab,
           int openSeats, int totalSeats) {

        this.courseCode  = courseCode;
        this.courseName  = courseName;
        this.professor   = professor;
        this.department  = department;
        this.location    = location;
        this.semester    = semester;
        this.startTime   = startTime;
        this.endTime     = endTime;
        this.days        = days;
        this.credits     = credits;
        this.isOpen      = isOpen;
        this.isLab       = isLab;
        this.openSeats   = openSeats;
        this.totalSeats  = totalSeats;
    }

    // Getters
    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getProfessor() {
        return professor;
    }

    public String getDepartment() {
        return department;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public int getCredits() {
        return credits;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isLab() {
        return isLab;
    }

    public int getOpenSeats() {
        return openSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public String getSemester() {
        return semester;
    }

    public String getLocation() {
        return location;
    }

}
