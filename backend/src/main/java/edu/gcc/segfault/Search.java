package edu.gcc.segfault;

import java.util.*;
import java.util.stream.Collectors;

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
     * Takes the list of keywords entered by the user (in the search bar). Makes a database
     * call, and adds every course that satisfies every keyword entered to the query that
     * is to be returned.
     *
     * @param searchKeywords ArrayList of words that the user has entered to search by.
     * @return A HashSet of courses that have a similarity to the searchKeywords ArrayList.
     */
    public Set<Course> fetchQuery(ArrayList<String> searchKeywords) throws Exception {
        Main search = new Main();
        search.run();
        //Get all courses from database.
        ArrayList<Course> allCourses = search.getCourses();

        Set<Course> query = new HashSet<>();
        if(searchKeywords.isEmpty()) return query;

        ArrayList<String> keywords = searchKeywords.stream().map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));

        for (Course toCheck : allCourses){
            //Split Course code, name, and professor names.
            String[] codeSplit = toCheck.getCourseCode().toLowerCase().split("-");
            String[] nameSplit = toCheck.getCourseName().toLowerCase().split(" ");
            String[] professorSplit = Arrays.stream(toCheck.getProfessor().toLowerCase().split(" ")).map(s -> s.replace(",", "")).toArray(String[]::new);

            //One boolean check to ensure all keywords match.
            boolean allKeysMatch = keywords.stream().allMatch(keyword ->
                    Arrays.stream(codeSplit).anyMatch((p -> p.contains(keyword))) ||
                            Arrays.stream(nameSplit).anyMatch(p -> p.contains(keyword)) ||
                            Arrays.stream(professorSplit).anyMatch(p -> p.contains(keyword))
            );
            if (allKeysMatch) query.add(toCheck);
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