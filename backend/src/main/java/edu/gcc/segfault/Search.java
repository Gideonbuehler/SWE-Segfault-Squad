package edu.gcc.segfault;

import java.util.ArrayList;
import java.util.Stack;

public class Search {
    private ArrayList<Course> originalResults;
    private Stack<ArrayList<Course>> history;
    private ArrayList<Filter> activeFilters;
    private ArrayList<String> searchKeywords;

    public ArrayList<Course> fetchQuery(){
        return new ArrayList<>();
    }

    public boolean applyFilters(){
        return false;
    }
    public ArrayList<Course> getResults(){
        return new ArrayList<>();
    }
}