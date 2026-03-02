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
     * @param courses
     * @return TRUE if filters apply successfully ? FALSE if it fails/the filters applied are the same as last time
     */
    public boolean applyFilters(ArrayList<Course> courses){

        ArrayList<Course> allCourses = Search.history.peek();

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

        Search.history.push(query);
        return true;
    }


}
