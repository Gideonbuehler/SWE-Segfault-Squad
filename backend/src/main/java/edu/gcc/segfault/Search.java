package edu.gcc.segfault;

import net.bytebuddy.asm.Advice;

import javax.security.auth.Subject;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Search {
    private Set<Course> originalResults;
    private Stack<Set<Course>> history;
    private ArrayList<Filter> activeFilters;
    private ArrayList<String> searchKeywords;
    private Connection conn;

    public Search(){
        this.originalResults = new HashSet<>();
        this.history = new Stack<>();
        this.activeFilters = new ArrayList<>();
        this.searchKeywords = new ArrayList<>();
        Supabase s = new Supabase();
        conn = s.getConn();
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
            String[] descriptionSplit = toCheck.getDescription() != null ? toCheck.getDescription().toLowerCase().split("\\s+") : new String[]{};

            //One boolean check to ensure all keywords match.
            boolean allKeysMatch = keywords.stream().allMatch(keyword ->
                    Arrays.stream(codeSplit).anyMatch((p -> p.contains(keyword))) ||
                            Arrays.stream(nameSplit).anyMatch(p -> p.contains(keyword)) ||
                            Arrays.stream(professorSplit).anyMatch(p -> p.contains(keyword)) ||
                            Arrays.stream(descriptionSplit).anyMatch(p -> p.contains(keyword))
            );
            if (allKeysMatch) query.add(toCheck);
        }

        history.push(query);
        originalResults = query;
        return query;
    }

    public Set<Course> fetchQueryDatabase(ArrayList<String> searchKeywords) throws SQLException {


        String preparedStatement = "SELECT * FROM CourseOfferings2 WHERE search_text ILIKE ?";
        //Used chatGPT to optimize and safeten the sql search
        for(int i = 1; i < searchKeywords.size(); i++) {
            preparedStatement += " OR search_text ILIKE ?";
        }

        PreparedStatement pstmt = conn.prepareStatement(preparedStatement);
        for(int i = 1; i <= searchKeywords.size(); i++) {
            pstmt.setString(i, "%" + searchKeywords.get(i - 1) + "%");
        }
        //end chatgpt direct influence
        Statement s = conn.createStatement();
        ResultSet rs = pstmt.executeQuery();

        Set<Course> returnedCourses = new HashSet<>();

        while(rs.next()){
            String times = rs.getString("times");
            LinkedHashMap<String, LocalTime[]> dayTimeMap = new LinkedHashMap<>();
            Scanner sc = new Scanner(times);
            sc.useDelimiter(";");
            while(sc.hasNext()){
                Scanner c = new Scanner(sc.next());
                String day = c.next();
                LocalTime start = LocalTime.parse(c.next());
                LocalTime end = LocalTime.parse(c.next());
            }
            //Course c = new Course(rs.getString("subject")+rs.getString("number") + rs.getString("section"), rs.getString("name"), rs.getString("professor"), rs.getString("subject"), rs.getString("location"), rs.getString("semester"), rs.getString(""));
            System.out.println("code: " + rs.getString("subject") + rs.getString("number") + " course name: " + rs.getString("name"));
        }

        return null;
    }

    public boolean applyFilters(){
        if (activeFilters.isEmpty() || history.isEmpty()) {
            return false;
        }

        Set<Course> currentResults = history.peek();

        // Loop through each filter set by frontend.
        /*
        John an example for front end might be that when a checkbox is checked something like this runs:
        Filter f = new Filter();
        f.setProfessorNames(new String[]{"Wolfe"});
        search.addFilter(f);

        then the filter result is updated and if they check another box it does this again.
         */
        Set<Course> filteredResults = new HashSet<>(currentResults);
        for (Filter filter : activeFilters) {
            filteredResults = filter.applyFilters(filteredResults);
        }

        if (filteredResults.equals(currentResults)) {
            return false;
        }

        history.push(filteredResults);
        return true;
    }

    /**
     * Adds a filter to the list of active filters
     * @param filter the filter to add.
     */
    public void addFilter(Filter filter) {
        this.activeFilters.add(filter);
    }

    /**
     * Removes a filter from the list of active filters
     * @param filter the filter to remove.
     */
    public void removeFilter(Filter filter) {
        this.activeFilters.remove(filter);
    }

    /**
     * Removes all filters and places the original database call to the top of the stack.
     */
    public void clearFilters(){
        history.push(originalResults);
    }

    /**
     * @return The top of the history stack, which is the most recent list of courses.
     */
    public Set<Course> getResults(){
        return history.peek();
    }
    public ArrayList<Filter> getActiveFilters() {
        return activeFilters;
    }

    public void popHistory(){
        history.pop();
    }

    public void setHistory(Set<Course> results){
        history.push(results);
    }

    public Set<Course> getOriginalResults(){
        return originalResults;
    }
}