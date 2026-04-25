package edu.gcc.segfault;

import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.*;
import java.util.HashSet;
import java.util.Set;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import io.javalin.Javalin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Controller {
//    public static User user = new User();
//
//    static {
//        user.setProfile(new Profile("FRESHMAN", "COMPUTER SCIENCE", new ArrayList<>(List.of("BUISINESS")), null));
//    }
    Dotenv dotenv = Supabase.dotenv;
    String apiKey = dotenv.get("RESEND_API_KEY");
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
            ctx.json(userService.getUser().getSchedule());
        });

        app.post("/mySchedule/add/{courseCode}/{semester}", ctx -> {
            String courseCode = ctx.pathParam("courseCode");
            String semester = ctx.pathParam("semester");

            Course toAdd = userService.getUser().getSchedule()
                    .findCourse(Main.getCourses(), courseCode, semester);

            if (toAdd == null) { ctx.status(404).result("Course not found"); return; }

            if (userService.getUser().getSchedule().addCourse(toAdd)) {
                userService.getUser().onScheduleChange();
                ctx.status(201).result("Course added");
            } else {
                ctx.status(500).result("Course conflict");
            }
        });

        app.delete("/mySchedule/remove/{courseCode}/{semester}", ctx -> {
            String courseCode = ctx.pathParam("courseCode");
            String semester = ctx.pathParam("semester");

            boolean removed = userService.getUser().getSchedule().removeCourseByCode(courseCode, semester);
            if (!removed) { ctx.status(404).result("Course not found in schedule"); return; }

            userService.getUser().onScheduleChange();
            ctx.status(200).result("Course removed");
        });

        app.post("/searchResults/{searchParameters}/filter", ctx -> {
            if (userService.getUser().getLastSearchResults() == null) {
                ctx.status(400).result("No search results to filter");
                return;
            }

            Filter filter = Filter.fromParams(
                    ctx.queryParam("department"),
                    ctx.queryParam("professor"),
                    ctx.queryParam("credits"),
                    ctx.queryParam("days"),
                    null, // no startTime in this route
                    null  // no endTime in this route
            );

            var results = userService.getUser().getLastSearchResults();
            results.addFilter(filter);
            results.applyFilters();
            ctx.json(results.getResults()).status(200);
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
                ctx.status(400).result("No search results to filter");
                return;
            }

            Filter filter = Filter.fromParams(
                    ctx.queryParam("department"),
                    ctx.queryParam("professor"),
                    ctx.queryParam("credits"),
                    ctx.queryParam("days"),
                    ctx.queryParam("startTime"),
                    ctx.queryParam("endTime")
            );

            var results = userService.getUser().getLastSearchResults();
            results.resetFilters();
            results.addFilter(filter);
            results.applyFilters();

            ctx.json(results.getResults()).status(200);
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

        app.get("/coursesInSlot", ctx -> {
            String day = ctx.queryParam("day");
            String startTime = ctx.queryParam("startTime");
            String endTime = ctx.queryParam("endTime");
            String semester = ctx.queryParam("semester");
            String keyword = ctx.queryParam("keyword");

            if (day == null || startTime == null || endTime == null || semester == null) {
                ctx.status(400);
                ctx.result("Missing required params");
                return;
            }

            try {
                Search s = new Search((new Supabase()).getConn());
                Set<Course> results = s.fetchCoursesInSlot(day, startTime, endTime, semester, keyword);
                Set<Course> noConflicts = userService.getUser().getSchedule().filterConflicts(results);
                ctx.json(noConflicts).status(200);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500);
                ctx.result("Search failed");
            }
        });


        app.get("/professors", ctx -> {
            String json = Files.readString(Path.of("professors.json"));
            ctx.contentType("application/json").result(json);
        });

        app.post("/email", ctx -> {
            System.out.println("Hit email backed");
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode body = mapper.readTree(ctx.body());

                String to = "marriottja21@gcc.edu";

                String subject = "Another student searched for you class";

                String message = "A student wants to take one of your classes, Dr. Hutchins!";

                String json = """
                {
                  "from": "onboarding@resend.dev",
                  "to": "%s",
                  "subject": "%s",
                  "html": "<p>%s</p>"
                }
                """.formatted(to, subject, message);
                System.out.println(apiKey);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.resend.com/emails"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                ctx.status(response.statusCode()).result(response.body());

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).json("{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }
}
