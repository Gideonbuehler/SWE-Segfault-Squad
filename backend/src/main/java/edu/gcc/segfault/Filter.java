package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;

public class Filter {
    private String professorName;
    private String departmentName;
    private int credits;
    private ArrayList<String> days;
    private LocalTime startTime;
    private LocalTime endTime;


    /**
     * This method retrieves the most recent search query (top of the stack named history in the Search class) and parses it over the filters specified by the user.
     * This generates a new list which is returned and show to the suer, as well as pushed to the top of the history stack.
     * @return TRUE if filters apply successfully ? FALSE if it fails/the filters applied are the same as last time
     */
    public boolean applyFilters(){

        // Check if there are any search results to filter
        if(Search.history.isEmpty()){
            return false;
        }

        // Get the most recent search results
        java.util.Set<Course> allCourses = Search.history.peek();

        // Create a new filtered set
        java.util.Set<Course> filteredQuery = new java.util.HashSet<>();

        // Iterate through all courses and apply filters
        for(Course toCheck : allCourses){
            // Check if course matches all applied filters
            if(matchesAllFilters(toCheck)){
                filteredQuery.add(toCheck);
            }
        }

        // Check if the filtered results are the same as before
        if(filteredQuery.equals(allCourses)){
            return false;
        }

        // Push the filtered results to history
        Search.history.push(filteredQuery);
        return true;
    }

    /**
     * Helper method to check if a course matches all active filters
     * @param course the course to check
     * @return TRUE if course matches all filters, FALSE otherwise
     */
    private boolean matchesAllFilters(Course course){
        // Filter by professor name (if specified)
        if(professorName != null && !professorName.isEmpty()){
            if(!course.getProfessor().equalsIgnoreCase(professorName)){
                return false;
            }
        }

        // Filter by department (if specified)
        if(departmentName != null && !departmentName.isEmpty()){
            if(!course.getDepartment().equalsIgnoreCase(departmentName)){
                return false;
            }
        }

        // Filter by credits (if specified, credits > 0 means it was set)
        if(credits > 0){
            if(course.getCredits() != credits){
                return false;
            }
        }

        // Filter by days (if specified)
        if(days != null && !days.isEmpty()){
            ArrayList<String> courseDays = course.getDays();
            // Check if the course has at least one matching day
            boolean dayMatch = false;
            for(String day : days){
                if(courseDays.contains(day)){
                    dayMatch = true;
                    break;
                }
            }
            if(!dayMatch){
                return false;
            }
        }

        // Filter by start time (if specified)
        if(startTime != null){
            if(!course.getStartTime().equals(startTime) && course.getStartTime().isBefore(startTime)){
                return false;
            }
        }

        // Filter by end time (if specified)
        if(endTime != null){
            if(!course.getEndTime().equals(endTime) && course.getEndTime().isAfter(endTime)){
                return false;
            }
        }

        return true;
    }


}
