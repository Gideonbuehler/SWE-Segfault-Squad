package edu.gcc.segfault;

import io.javalin.Javalin;

import java.util.ArrayList;

public class Controller {
    public static User user = new User();

    static {
        ArrayList<String> tempMinors = new ArrayList<>();
        tempMinors.add("Buisiness");
        user.setProfile(new Profile("Freshman", "Computer Science", tempMinors, null));
        user.setSchedule(new Schedule("F25"));  // Claude
    }
    public static void routeManager (Javalin app){
        // routes for search pages
        app.get("/searchResults", ctx -> ctx.json(user.getLastSearchResults()));

        app.post("/searchResults/{searchParameters}", ctx -> {
            String results = ctx.pathParam("searchParameters");
            ctx.json(user.searchCourses(results));
            ctx.status(201);
        });


        //routes for profile
        app.get("/profile", ctx -> ctx.json(user.getProfile()));
        //Structure this by a route for each thing to change?
        //allow the user to update their major
        app.post("/profile/major/{major}", ctx -> {
            String change = ctx.pathParam("major");
            if(user.getProfile().updateMajor(change)){
                ctx.status(201);
            }
            else{
                ctx.status(400);
            }
        });
        //update minors one at a time
        app.post("/profile/minors/{minors}", ctx -> {
            String change = ctx.pathParam("minors");
            if(user.getProfile().addMinor(change)){
                ctx.status(201);
            }

        });
        app.delete("/profile/minors/{minor}", ctx -> {
            String change = ctx.pathParam("minor");
            if(user.getProfile().deleteMinor(change)){
                ctx.status(201);
            }
        });
        //update graduation year
        app.post("/profile/{year}", ctx -> {
            String change = ctx.pathParam("year");
            if(user.getProfile().updateYear(change)){
                ctx.status(201);
            }
            else{
                ctx.status(400);
            }
        });
        //Need to update the completed courses by a list or one at a time?
        app.post("/profile/{completedCourses}", ctx -> {
            String change = ctx.pathParam("completedCourses");
            if(user.getProfile().updateYear(change)){
                ctx.status(201);
            }
            else{
                ctx.status(400);
            }
        });

        //routes for calendar
        //Need to get the calendar from the schedule?
        app.get("/calendar", ctx -> ctx.json(user.getSchedule().getCalendar()));

        //routes for schedule
        app.get("/mySchedule", ctx -> ctx.json(user.getSchedule()));

        app.post("/mySchedule/add/{courseCode}", ctx -> {
            String courseCode = ctx.pathParam("courseCode");
            ArrayList<Course> allCourses = Main.getCourses();

            Course toAdd = null;
            for (Course c : allCourses) {
                if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                    toAdd = c;
                    break;
                }
            }

            if (toAdd == null) {
                ctx.status(404);
                ctx.result("Course not found");
                return;
            }

            user.getSchedule().addCourse(toAdd);
            ctx.status(201);
            ctx.result("Course added");
        });
    }
}
