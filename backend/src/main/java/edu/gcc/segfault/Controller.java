package edu.gcc.segfault;

import io.javalin.Javalin;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalTime;
import java.util.*;

public class Controller {
//    public static User user = new User();
//
//    static {
//        user.setProfile(new Profile("FRESHMAN", "COMPUTER SCIENCE", new ArrayList<>(List.of("BUISINESS")), null));
//    }
    private final UserService userService;

    public Controller(UserService userService) {
        this.userService = userService;
    }
    public void routeManager (Javalin app){
        // routes for search pages
        app.get("/searchResults", ctx -> {
            System.out.println("HIT BACKEND");
            ctx.json(userService.getUser().getLastSearchResults());
        });

        app.post("/searchResults/{searchParameters}", ctx -> {
            String results = ctx.pathParam("searchParameters");
            System.out.println("HIT BACKEND");
            ctx.json(userService.getUser().searchCourses(results));
            ctx.status(201);
        });


        //routes for profile
        app.get("/profile", ctx -> {try {
            if(userService.getUser() != null) {
                ctx.json(userService.getUser().getProfile());
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("JSON ERROR: " + e.getMessage());
        }});
        //Structure this by a route for each thing to change?
        //allow the user to update their major
        app.post("/profile/major/{major}", ctx -> {
            String change = ctx.pathParam("major");
            if(userService.getUser().getProfile().updateMajor(change)){
                ctx.status(201);
            }
            else{
                ctx.status(400);
            }
        });
        app.post("/profile/minor/{minor}", ctx -> {
            try {
                System.out.println("Route hit");

                var user = userService.getUser();
                System.out.println("User: " + user);

                var profile = user.getProfile();
                System.out.println("Profile: " + profile);

                String change = ctx.pathParam("minor");
                System.out.println("Minor: " + change);

                boolean updated = profile.updateMinor(change);
                System.out.println("Printing minor:" + user.getProfile().getMinor());
                System.out.println("Updated: " + updated);
                System.out.println("I can print after updated");

                if (updated) {
                    ctx.status(201).json(Map.of("message", "ok"));
                } else {
                    ctx.status(400).json(Map.of("error", "invalid"));
                }


            } catch (Exception e) {
                e.printStackTrace(); // 👈 THIS IS THE GOLD
                ctx.status(500).json(Map.of("error", "server crash"));
            }
        });
        //update minors one at a time
        //AI to debug
        app.post("/api/profile/minors/{minors}", ctx -> {
            try {
                String change = ctx.pathParam("minors");

                System.out.println("user: " + userService.getUser());
                System.out.println("profile: " + (userService.getUser() != null ? userService.getUser().getProfile() : "user is null"));

                boolean added = userService.getUser().getProfile().addMinor(change);

                if (added) {
                    ctx.status(200).json(userService.getUser().getProfile());
                } else {
                    ctx.status(400).result("Minor already exists");
                }

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server Error");
            }
        });
        app.delete("/profile/minors/{minor}", ctx -> {
            String change = ctx.pathParam("minor");
            if(userService.getUser().getProfile().deleteMinor(change)){
                System.out.println("deleted " + change);
                ctx.status(200);
            }
            else {
                ctx.status(404).result("Minor was not removed from the list");
            }
        });
        //update graduation year
        app.post("/profile/year/{year}", ctx -> {
            String change = ctx.pathParam("year");
            if(userService.getUser().getProfile().updateYear(change)){
                ctx.status(201);
            }
            else{
                ctx.status(400);
            }
        });
        //Need to update the completed courses by a list or one at a time?
        app.post("/profile/completedCourses/{completedCourses}", ctx -> {
            String change = ctx.pathParam("completedCourses");
            if(userService.getUser().getProfile().updateYear(change)){
                ctx.status(201);
            }
            else{
                ctx.status(400);
            }
            ctx.json(userService.getUser().getProfile());
        });

        //routes for calendar
        //Need to get the calendar from the schedule?
        app.get("/calendar", ctx -> ctx.json(userService.getUser().getSchedule().getCalendar()));

        //routes for schedule
        app.get("/mySchedule", ctx -> {
            User user = new User();
            var schedule = user.getSchedule();
            ctx.json(schedule != null ? schedule : Collections.emptyList());
        });
        app.post("/mySchedule/add/{courseCode}/{semester}", ctx -> {
            String courseCode = ctx.pathParam("courseCode");
            String semester = ctx.pathParam("semester");
            ArrayList<Course> allCourses = Main.getCourses();
            System.out.println("HIT BACKEND");
            Course toAdd = null;
            for (Course c : allCourses) {
                if (c.getCourseCode().equalsIgnoreCase(courseCode)
                        && c.getSemester().equalsIgnoreCase(semester)) {
                    toAdd = c;
                    break;
                }
            }

            // Change to enum!!!
            if (toAdd == null) {
                ctx.status(404);
                ctx.result("Course not found");
                return;
            }

            if (userService.getUser().getSchedule().addCourse(toAdd)) {
                userService.getUser().onScheduleChange();
                ctx.status(201);
                ctx.result("Course added");
                return;
            }
            else {
                ctx.status(500);
                ctx.result("Course conflict");
                return;
            }
            //Add specific message for full class?
        });

        app.delete("/mySchedule/remove/{courseCode}/{semester}", ctx -> {
            String courseCode = ctx.pathParam("courseCode");
            String semester = ctx.pathParam("semester");
            ArrayList<Course> courses = userService.getUser().getSchedule().getCourses();
            Course toRemove = null;
            for (Course c : courses) {
                if (c.getCourseCode().equalsIgnoreCase(courseCode)
                        && c.getSemester().equalsIgnoreCase(semester)) {
                    toRemove = c;
                    break;
                }
            }

            if (toRemove == null) {
                ctx.status(404);
                ctx.result("Course not found in schedule");
                return;
            }

            userService.getUser().getSchedule().removeCourse(toRemove);
            userService.getUser().onScheduleChange();
            ctx.status(200);
            ctx.result("Course removed");
        });

        app.post("/searchResults/{searchParameters}/filter", ctx -> {

            if (userService.getUser().getLastSearchResults() == null) {
                ctx.status(400);
                ctx.result("No search results to filter");
                return;
            }

            Filter filter = new Filter();

            String department = ctx.queryParam("department");
            String professor = ctx.queryParam("professor");
            String credits = ctx.queryParam("credits");
            String days = ctx.queryParam("days");
            String description = ctx.queryParam("description");

            if (department != null && !department.isEmpty())
                filter.setDepartmentNames(new String[]{department});

            if (professor != null && !professor.isEmpty())
                filter.setProfessorNames(new String[]{professor});

            if (credits != null && !credits.isEmpty())
                filter.setCredits(new int[]{Integer.parseInt(credits)});

            if (days != null && !days.isEmpty()) {
                ArrayList<String> dayList = new ArrayList<>(Arrays.asList(days.split(",")));
                filter.setDays(dayList);
            }

            if (description != null && !description.isEmpty())
                filter.setDescriptionKeywords(new String[]{description});

            userService.getUser().getLastSearchResults().addFilter(filter);
            userService.getUser().getLastSearchResults().applyFilters();
            ctx.json(userService.getUser().getLastSearchResults().getResults());


            ctx.status(200);
        });

        app.get("/noFilters", ctx -> {
            if (userService.getUser().getLastSearchResults() == null) {
                ctx.status(400);
                ctx.result("No search results to filter");
                return;
            }

            ctx.json(userService.getUser().getLastSearchResults().getOriginalResults());
            ctx.status(200);
        });

        app.post("/filterResults/{searchParameters}/filter", ctx -> {
            if (userService.getUser().getLastSearchResults() == null) {
                ctx.status(400);
                ctx.result("No search results to filter");
                return;
            }

            Filter filter = new Filter();

            String department = ctx.queryParam("department");
            String professor = ctx.queryParam("professor");
            String credits = ctx.queryParam("credits");
            String days = ctx.queryParam("days");
            String startTime = ctx.queryParam("startTime");
            String endTime = ctx.queryParam("endTime");

            if (department != null && !department.isEmpty())
                filter.setDepartmentNames(new String[]{department});

            if (professor != null && !professor.isEmpty())
                filter.setProfessorNames(new String[]{professor});

            if (credits != null && !credits.isEmpty())
                filter.setCredits(new int[]{Integer.parseInt(credits)});

            if (days != null && !days.isEmpty()) {
                ArrayList<String> dayList = new ArrayList<>(Arrays.asList(days.split(",")));
                filter.setDays(dayList);
            }

            if (startTime != null && !startTime.isEmpty())
                filter.setStartTimes(new LocalTime[]{LocalTime.parse(startTime)});

            if (endTime != null && !endTime.isEmpty())
                filter.setEndTimes(new LocalTime[]{LocalTime.parse(endTime)});


            userService.getUser().getLastSearchResults().clearFilters();
            userService.getUser().getLastSearchResults().getActiveFilters().clear();
            userService.getUser().getLastSearchResults().addFilter(filter);
            userService.getUser().getLastSearchResults().applyFilters();
            ctx.json(userService.getUser().getLastSearchResults().getResults());

            ctx.status(200);
        });


        app.get("/mySchedule/pdf", ctx -> {
            try {
                userService.getUser().getSchedule().makePDF();
                File pdfFile = new File("Schedule.pdf");
                ctx.contentType("application/pdf");
                ctx.header("Content-Disposition", "attachment; filename=Schedule.pdf");
                ctx.result(new FileInputStream(pdfFile));
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500);
                ctx.result("Failed to generate PDF");
            }
        });
    }
}
