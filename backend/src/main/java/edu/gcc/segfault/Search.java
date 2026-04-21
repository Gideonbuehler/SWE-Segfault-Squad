package edu.gcc.segfault;

import java.sql.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


public class Search {
    private Set<Course> originalResults;
    private Stack<Set<Course>> history;
    private ArrayList<Filter> activeFilters;
    private ArrayList<String> searchKeywords;
    private Connection conn = null;

    public Search(){
        this.originalResults = new HashSet<>();
        this.history = new Stack<>();
        this.activeFilters = new ArrayList<>();
        this.searchKeywords = new ArrayList<>();

    }
    public Search(Connection conn){
        this.originalResults = new HashSet<>();
        this.history = new Stack<>();
        this.activeFilters = new ArrayList<>();
        this.searchKeywords = new ArrayList<>();
        this.conn = conn;
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

    public Set<Course> fetchQueryDatabase(ArrayList<String> searchKeywords) {
        Set<Course> returnedCourses = new HashSet<>();
        StringBuilder preparedStatement = new StringBuilder("SELECT * FROM courseofferings2 WHERE search_text ILIKE ?");
        //Used chatGPT to optimize and safeten the sql search
        for (int i = 1; i < searchKeywords.size(); i++) {
            preparedStatement.append(" OR search_text ILIKE ?");
        }
        try {
            PreparedStatement pstmt = conn.prepareStatement(preparedStatement.toString());
            for (int i = 1; i <= searchKeywords.size(); i++) {
                pstmt.setString(i, "%" + searchKeywords.get(i - 1) + "%");
            }
            //end chatgpt direct influence
            Statement s = conn.createStatement();
            //Get results
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                //fix times to the correct data structure
                String times = rs.getString("times");
                LinkedHashMap<String, LocalTime[]> dayTimeMap = new LinkedHashMap<>();
                if (times != null && !times.isEmpty()) {
                    String[] entries = times.split(";");

                    for (String entry : entries) {
                        String[] parts = entry.trim().split("\\s+");

                        if (parts.length == 3) {
                            String day = parts[0];
                            LocalTime start = LocalTime.parse(parts[1]);
                            LocalTime end = LocalTime.parse(parts[2]);

                            dayTimeMap.put(day, new LocalTime[]{start, end});
                        }
                    }
                }

                //create the course from the row of data
                Course c = new Course(rs.getString("subject") + "-" + rs.getString("number") + "-" +  rs.getString("section"), rs.getString("name"), rs.getString("faculty"), rs.getString("subject"), rs.getString("location"), rs.getString("semester"), dayTimeMap, rs.getInt("credits"), rs.getBoolean("is_open"), rs.getBoolean("is_lab"), rs.getInt("open_seats"), rs.getInt("total_seats"), rs.getString("description"));
                returnedCourses.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        history.push(returnedCourses);
        originalResults = returnedCourses;
        return returnedCourses;

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