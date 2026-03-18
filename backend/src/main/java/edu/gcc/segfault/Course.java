package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Course {
    private String courseCode;
    private String courseName;
    private String location;
    private String professor;
    private String department;
    private LinkedHashMap<String, LocalTime[]> dayTimeMap;
    private int credits;
    private String semester;
    private Boolean isOpen;
    private Boolean isLab;
    private int openSeats;
    private int totalSeats;
    private String description;

    //Default constructor for tests
    public Course() {
        this.dayTimeMap = new LinkedHashMap<>();
    }

    // Constructor for main
    public Course(String courseCode, String courseName, String professor, String department,
           String location, String semester, LinkedHashMap<String, LocalTime[]> dayTimeMap, int credits, boolean isOpen, boolean isLab,
           int openSeats, int totalSeats, String description) {

        this.courseCode  = courseCode;
        this.courseName  = courseName;
        this.professor   = professor;
        this.department  = department;
        this.location    = location;
        this.semester    = semester;
        this.dayTimeMap  = dayTimeMap;
        this.credits     = credits;
        this.isOpen      = isOpen;
        this.isLab       = isLab;
        this.openSeats   = openSeats;
        this.totalSeats  = totalSeats;
        this.description = description;
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

    public LinkedHashMap<String, LocalTime[]> getDayTimeMap() {
        return dayTimeMap;
    }

    public int getCredits() {
        return credits;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public boolean getIsLab() {
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

    public String getDescription() {
        return description;
    }


    // Equals and hashCode overriden to ease comparison of Blocks
    // (especially useful in Block.equals() override)
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Course course)) return false;
        return credits == course.credits && totalSeats == course.totalSeats && Objects.equals(courseCode, course.courseCode) && Objects.equals(courseName, course.courseName) &&
                Objects.equals(location, course.location) && Objects.equals(professor, course.professor) && Objects.equals(department, course.department) &&
                Objects.equals(dayTimeMap, course.dayTimeMap) && Objects.equals(semester, course.semester) && Objects.equals(isLab, course.isLab) && Objects.equals(description, course.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, courseName, location, professor, department, dayTimeMap, credits, semester, isOpen, isLab, openSeats, totalSeats, description);
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", professor='" + professor + '\'' +
                ", dayTimeMap=" + dayTimeMap + '\'' +
                ", description=" + description +
                '}';
    }
}
