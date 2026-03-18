package edu.gcc.segfault;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Filter {
    private String[] professorNames;
    private String[] departmentNames;
    private int[] credits;
    private ArrayList<String> days;
    private LocalTime[] startTimes;
    private LocalTime[] endTimes;


    /**
     * Applies all active filters to a set of courses and returns the filtered results.
     * A course is included in the results only if it matches ALL active filter criteria.
     * For array-based filters, a course must match AT LEAST ONE value in that array.
     *
     * @param courses the Set of courses to filter
     * @return a filtered Set of courses that match all active filter criteria
     */
    public Set<Course> applyFilters(Set<Course> courses){
        Set<Course> filtered = new HashSet<>(courses);


        // Removes a course from the list if it does not match at least one name provided.
        if (professorNames != null && professorNames.length > 0) {
            filtered.removeIf(course -> {
                String courseProf = course.getProfessor().toLowerCase();
                return Arrays.stream(professorNames)
                        .map(String::toLowerCase)
                        .noneMatch(courseProf::contains);
            });
        }

        // Removes a course from the list if it does not match at least one dept provided.
        if (departmentNames != null && departmentNames.length > 0) {
            filtered.removeIf(course -> {
                String courseDept = course.getDepartment().toLowerCase();
                return Arrays.stream(departmentNames)
                        .map(String::toLowerCase)
                        .noneMatch(dept -> courseDept.equals(dept));
            });
        }

        //  Removes a course from the list if it does not match at least credit amounts provided.
        if (credits != null && credits.length > 0) {
            filtered.removeIf(course -> {
                int courseCredits = course.getCredits();
                return Arrays.stream(credits)
                        .noneMatch(credit -> credit == courseCredits);
            });
        }

        // Removes a course from the list if it does not match the days provided.
        // Because of the way the JSON parse is set up the days are should be in
        // a list format. i.e MWF -> ["M", "W", "F"]. This filter snip will then check for
        // all the days.
        if (days != null && !days.isEmpty()) {
            filtered.removeIf(course -> {
                ArrayList<String> courseDays = (ArrayList<String>) course.getDayTimeMap().keySet();
                return !courseDays.containsAll(days);
            });
        }

        // Removes a course from the list if it does not match the start time provided.
        // Should be paired with the end time.
        if (startTimes != null && startTimes.length > 0) {
            filtered.removeIf(course -> {
                if (course.getDayTimeMap().firstEntry().getValue()[0] == null) return true;
                return Arrays.stream(startTimes)
                        .noneMatch(time -> course.getDayTimeMap().firstEntry().getValue()[0].equals(time));
            });
        }

        // Removes a course from the list if it does not match the end time provided.
        // Should be paired with the start time.
        if (endTimes != null && endTimes.length > 0) {
            filtered.removeIf(course -> {
                if (course.getDayTimeMap().firstEntry().getValue()[1]== null) return true;
                return Arrays.stream(endTimes)
                        .noneMatch(time -> course.getDayTimeMap().firstEntry().getValue()[1].equals(time));
            });
        }

        //All courses that had a match with all filters provided.
        return filtered;
    }

    /*
    Setters for frontend, when the user enters or checks filters it should add the filter(s) with these setters.
     */
    public void setProfessorNames(String[] professorNames) {
        this.professorNames = professorNames;
    }

    public void setDepartmentNames(String[] departmentNames) {
        this.departmentNames = departmentNames;
    }

    public void setCredits(int[] credits) {
        this.credits = credits;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public void setStartTimes(LocalTime[] startTimes) {
        this.startTimes = startTimes;
    }

    public void setEndTimes(LocalTime[] endTimes) {
        this.endTimes = endTimes;
    }
}
