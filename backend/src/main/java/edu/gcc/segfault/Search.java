package edu.gcc.segfault;

import java.util.ArrayList;
import java.util.Stack;
//import com.fasterxml.jackson.databind.JsonNode; only if using node stuff


public class Search {
    private ArrayList<Course> originalResults;
    private Stack<ArrayList<Course>> history;
    private ArrayList<Filter> activeFilters;
    private ArrayList<String> searchKeywords;

    public Search(){
        this.originalResults = new ArrayList<>();
        this.history = new Stack<>();
        this.activeFilters = new ArrayList<>();
        this.searchKeywords = new ArrayList<>();
    }

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