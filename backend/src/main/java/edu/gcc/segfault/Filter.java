package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;

public class Filter {
    private String professorName;
    private String departmentName;
    private int credits;
    private ArrayList<String> days;
    private LocalTime startTime;
    private LocalTime endTime;

    public boolean applyFilters(ArrayList<Course> courses){
        return false;
    }


}
