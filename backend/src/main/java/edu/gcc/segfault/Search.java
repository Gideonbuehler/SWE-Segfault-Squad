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

    /**
     * @param searchKeywords
     * @return an ArrayList of courses that have a similarity to the searchKeywords ArrayList
     */
    public ArrayList<Course> fetchQuery(ArrayList<String> searchKeywords){
        //stub code return new ArrayList<>();

        //Course code
        //Course name
        //Prof name
        //Dept

        ArrayList<Course> allCourses = Main.getCourses();

        ArrayList<Course> query = new ArrayList<>();

        for(int c = 0; c<allCourses.size(); c++){
            Course toCheck = allCourses.get(c);
            String code = toCheck.getCourseCode();
            String name = toCheck.getCourseName();
            String professor = toCheck.getProfessor();
            String department = toCheck.getDepartment();
            for(int k=0; k<searchKeywords.size(); k++){
                if(code.equalsIgnoreCase(searchKeywords.get(k)) || name.equalsIgnoreCase(searchKeywords.get(k)) || professor.equalsIgnoreCase(searchKeywords.get(k)) || department.equalsIgnoreCase(searchKeywords.get(k))){
                    query.add(toCheck);
                    break;
                }
            }
        }

        history.push(query);
        return query;
    }

    public boolean applyFilters(){
        return false;
    }

    public ArrayList<Course> getResults(){
        return new ArrayList<>();
    }
}