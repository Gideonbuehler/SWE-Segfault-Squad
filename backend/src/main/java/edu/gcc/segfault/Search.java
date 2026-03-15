package edu.gcc.segfault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Search {
    private Set<Course> originalResults;
    private Stack<Set<Course>> history;
    private ArrayList<Filter> activeFilters;
    private ArrayList<String> searchKeywords;

    public Search(){
        this.originalResults = new HashSet<>();
        this.history = new Stack<>();
        this.activeFilters = new ArrayList<>();
        this.searchKeywords = new ArrayList<>();
    }

    /**
     * This method compares the entered search terms to each class in the database's information, resulting
     * in search results that are fulfilled by all the search terms
     * @param searchKeywords ArrayList of words that the user has entered to search by.
     * @return A set of courses that have a similarity to the searchKeywords ArrayList
     */
    public Set<Course> fetchQuery(ArrayList<String> searchKeywords) throws Exception {
        //uncomment these lines and comment line 36, to run the SearchTest alone instead of having the gradle build do it.
        Main search = new Main();
        search.run();
        //Get al courses from database.
        ArrayList<Course> allCourses = search.getCourses();
        //ArrayList<Course> allCourses = Main.getCourses();

        Set<Course> query = new HashSet<>();
        if(searchKeywords.isEmpty()){
            return query;
        }

        for(int c = 0; c<allCourses.size(); c++){
            Course toCheck = allCourses.get(c);
            String code = toCheck.getCourseCode();
            String[] codeSplit = code.split("-");
            String name = toCheck.getCourseName();
            String[] nameSplit = name.split(" ");
            String professor = toCheck.getProfessor();
            String[] professorSplit = professor.split(" ");

            //Cleans professor names of commas.
            for (int i = 0; i < professorSplit.length; i++) {
                if(professorSplit[i].contains(",")){
                    professorSplit[i] = professorSplit[i].replace(",", "");
                }
            }
            String department = toCheck.getDepartment();


            int keywordCheck = 0;
            for(int k=0; k<searchKeywords.size(); k++){
                boolean found = false;
                //tests for each part of the course code ie COMP, 141, and A
                for (int l = 0; l < codeSplit.length; l++) {
                    if ((codeSplit[l].toUpperCase()).contains(searchKeywords.get(k).toUpperCase())) {
                        keywordCheck++;
                        found = true;
                        break;
                    }
                }
                //Next keyword.
                if (found){
                    continue;
                }
                //Test for each part of the name
                for (int n = 0; n < nameSplit.length; n++)
                {
                    if(nameSplit[n].contains(searchKeywords.get(k)) || nameSplit[n].equalsIgnoreCase(searchKeywords.get(k))){
                        keywordCheck++;
                        found = true;
                        break;
                    }
                }
                //Next keyword.
                if (found){
                    continue;
                }
                for (int p = 0; p < professorSplit.length; p++)
                {
                    if(professorSplit[p].contains(searchKeywords.get(k))|| professorSplit[p].equalsIgnoreCase(searchKeywords.get(k))){
                        keywordCheck++;
                        break;
                    }
                }
            }
            //Makes sure that the course is applicable to all the search terms
            if(keywordCheck >= searchKeywords.size())
                query.add(toCheck);
        }

        history.push(query);
        originalResults = query;
        return query;
    }

    public boolean applyFilters(){
        return false;
    }

    /**
     * @return The top of the history stack, which is the most recent search results.
     */
    public Set<Course> getResults(){
        return history.peek();
    }
}