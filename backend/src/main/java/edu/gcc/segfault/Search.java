package edu.gcc.segfault;

import java.util.ArrayList;
import java.util.Stack;

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
    public ArrayList<Course> fetchQuery(ArrayList<String> searchKeywords) throws Exception {
        //stub code return new ArrayList<>();

        //Course code
        //Course name
        //Prof name
        //Dept


//        uncomment these lines and comment line 36, to run the SearchTest alone instead of having the gradle build do it.
        Main search = new Main();
        search.run();
        ArrayList<Course> allCourses = search.getCourses();
        //ArrayList<Course> allCourses = Main.getCourses();

        ArrayList<Course> query = new ArrayList<>();

        for(int c = 0; c<allCourses.size(); c++){
            Course toCheck = allCourses.get(c);
            String code = toCheck.getCourseCode();
            String[] codeSplit = code.split("-");
            String name = toCheck.getCourseName();
            String[] nameSplit = name.split(" ");
            String professor = toCheck.getProfessor();
            String[] professorSplit = professor.split(" ");
            for (int i = 0; i < professorSplit.length; i++) {
                if(professorSplit[i].contains(",")){
                    professorSplit[i] = professorSplit[i].replace(",", "");
                }
            }

            String department = toCheck.getDepartment();

            for(int k=0; k<searchKeywords.size(); k++){
                //tests for each part of the course code ie COMP, 141, and A
                for (int l = 0; l < codeSplit.length; l++) {
                    if (codeSplit[l].contains(searchKeywords.get(k))) {
                        query.add(toCheck);
                        break;
                    }
                }
                for(String n : nameSplit){
                    if(n.contains(searchKeywords.get(k))){
                        query.add(toCheck);
                        break;
                    }
                }
                for(String p : professorSplit){
                    if(p.contains(searchKeywords.get(k))){
                        query.add(toCheck);
                        break;
                    }
                }
                if(department.equalsIgnoreCase(searchKeywords.get(k))){
                    query.add(toCheck);
                    break;
                }
            }
        }

        history.push(query);
        originalResults = query;  // Claude
        return query;
    }

    public boolean applyFilters(){
        return false;
    }

    public ArrayList<Course> getResults(){
        return originalResults;
    }
}