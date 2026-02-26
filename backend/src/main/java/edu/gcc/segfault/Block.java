package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.Objects;

public class Block {
    private Course course;
    private LocalTime startTime;
    private LocalTime endTime;

    public Block() {
        this.course = null;
        this.startTime = null;
        this.endTime = null;
    }

    public Block(Course course) {
        this.course = course;
        this.startTime = course.getStartTime();
        this.endTime = course.getEndTime();
    }

    //Getters and Setters
    public Course getCourse() {
        return course;
    } //Test comment

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;

        Block block = (Block) o;

        return Objects.equals(startTime, block.startTime) &&
                Objects.equals(endTime, block.endTime) &&
                Objects.equals(course, block.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, course);
    }
}

