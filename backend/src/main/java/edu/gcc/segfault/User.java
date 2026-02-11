package edu.gcc.segfault;

public class User {
    private String userName;
    private Schedule schedule;
    private Profile profile;
    private Search lastSearchResults;


    public void addCourse(Course toAdd){

    }

    public void dropCourse(Course toDrop){

    }

    public Search searchCourses(String search){
        Search s = new Search();
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
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
