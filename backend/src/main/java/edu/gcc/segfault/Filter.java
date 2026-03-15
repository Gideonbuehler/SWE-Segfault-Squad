package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Filter {
    private String[] professorName;
    private String[] departmentName;
    private int[] credits;
    private ArrayList<String> days;
    private LocalTime[] startTime;
    private LocalTime[] endTime;


    /**
     * This method retrieves the most recent search query (top of the stack named history in the Search class) and parses it over the filters specified by the user.
     * This generates a new list which is returned and show to the suer, as well as pushed to the top of the history stack.
     * @return TRUE if filters apply successfully ? FALSE if it fails/the filters applied are the same as last time
     */
    public boolean applyFilters(Set<Course> courses){

        Set<Course> toFilter = Search.getResults();

        Set<Course> filtered = new HashSet<>();


        for (Course toCheck : toFilter) {
            //Split Course code, name, and professor names.
            String[] codeSplit = toCheck.getCourseCode().toLowerCase().split("-");
            String[] nameSplit = toCheck.getCourseName().toLowerCase().split(" ");
            String[] professorSplit = Arrays.stream(toCheck.getProfessor().toLowerCase().split(" ")).map(s -> s.replace(",", "")).toArray(String[]::new);

            //One boolean check to ensure all keywords match.
            boolean allFiltersMatch = filters.stream().allMatch(keyword ->
                    Arrays.stream(codeSplit).anyMatch((p -> p.contains(filter))) ||
                            Arrays.stream(nameSplit).anyMatch(p -> p.contains(filter)) ||
                            Arrays.stream(professorSplit).anyMatch(p -> p.contains(filter))
            );

        }
        return false;
    }


}
