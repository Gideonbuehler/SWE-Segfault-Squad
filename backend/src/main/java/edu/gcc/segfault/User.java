package edu.gcc.segfault;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class User {
    private String userName;
    private Schedule schedule;
    private Profile profile;
    private Search lastSearchResults;
    private Set<Course> originalResults;


    public User(){
        schedule = new Schedule("F25");
    }
    public void addCourse(Course toAdd){

    }

    public void dropCourse(Course toDrop){

    }

    public Search searchCourses(String search) {
        Search s = new Search();
        try {
            ArrayList<String> keywords = new ArrayList<>();
            //clean the search words
            String[] searchSplit = search.split(" ");
            for (int i = 0; i < searchSplit.length; i++) {
                searchSplit[i] = searchSplit[i].replaceAll("\\p{Punct}", "");
            }
            keywords.addAll(List.of(searchSplit));
            s.fetchQuery(keywords);  // this populates the history stack
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastSearchResults = s;
        originalResults = s.getOriginalResults();
        return s;
    }

    //Getters and Setters

    public Search getLastSearchResults() {
        return lastSearchResults;
    }

    public void setLastSearchResults(Search lastSearchResults) {
        this.lastSearchResults = lastSearchResults;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        this.schedule.loadSchedule(); //Restore save file if it exists
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
