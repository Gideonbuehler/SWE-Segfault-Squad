package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;

public class Course {
    private String courseCode;
    private String courseName;
    private String description;
    private String professor;
    private String department;
    private LocalTime startTime;
    private LocalTime endTime;
    private ArrayList<String> days;
    private int credits;

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDescription() {
        return description;
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

}
