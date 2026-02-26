package edu.gcc.segfault;

import io.javalin.Javalin;

public class Controller {
    public static User user = new User();
    public static void routeManager (Javalin app){
        // routes for search pages
        app.get("/searchResults", ctx -> ctx.json(user.getLastSearchResults()));

        app.post("/searchResults", ctx -> {
            user.searchCourses(ctx.body());
            ctx.status(201); // 201 means “created”
        });


        //routes for profile
        app.get("/profile", ctx -> ctx.json(user.getProfile()));


        //routes for calendar
        //Need to get the calendar from the schedule?
        app.get("/calendar", ctx -> ctx.json(user.getSchedule()));
    }
}
