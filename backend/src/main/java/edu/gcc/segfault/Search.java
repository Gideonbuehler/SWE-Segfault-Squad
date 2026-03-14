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
     * @param searchKeywords
     * @return an ArrayList of courses that have a similarity to the searchKeywords ArrayList
     */
    public Set<Course> fetchQuery(ArrayList<String> searchKeywords) throws Exception {
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

        Set<Course> query = new HashSet<>();

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

                //Department is already covered through
//                if(department.contains(searchKeywords.get(k)) || department.equalsIgnoreCase(searchKeywords.get(k))){
//                    keywordCheck++;
//                    break;
//                }
            }
            if(keywordCheck >= searchKeywords.size())
                query.add(toCheck);
        }

        history.push(query);
        originalResults = query;// Claude
        return query;
    }

    public boolean applyFilters(){
        return false;
    }

    public Set<Course> getResults(){
        return originalResults;
    }
}