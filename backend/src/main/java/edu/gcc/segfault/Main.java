package edu.gcc.segfault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Main {
    private static ArrayList<Course> courses;

    // runs the parser
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());  // ADD THIS

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson());  // tell Javalin to use it
        }).start(7000);
        Controller.routeManager(app);

        Main main = new Main();
        main.run();
    }
    public void run() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("data_wolfe.json"));
        courses = loadAll(root);
    }

    private static ArrayList<Course> loadAll(JsonNode root) {
        // Create arraylist for courses
        ArrayList<Course> courseList = new ArrayList<>();

        JsonNode classes = root.get("classes");
        if (classes == null) return courseList;

        // Iterate through every course in classes
        for (JsonNode node : classes) {
            Course c = fromJson(node);
            if (c != null) courseList.add(c);
        }

        return courseList;
    }

    public static Course fromJson(JsonNode node) {
        if (node == null) return null;

        try {
            // Get all the course info from JSON, made then ternaries in case the values dont exist
            String subject = node.has("subject") ? node.get("subject").asText() : "UNKNOWN";
            int number = node.has("number") ? node.get("number").asInt() : 0;
            String section = node.has("section") ? node.get("section").asText() : "?";

            // ie BIO-205-B
            String courseCode = subject + "-" + number + "-" + section; // Not sure if we want to keep this

            String courseName = node.has("name") ? node.get("name").asText() : "Unknown Course";
            String department = subject;
            int credits = node.has("credits") ? node.get("credits").asInt() : 0;
            String location = node.has("location") ? node.get("location").asText() : "TBA";
            String semester = node.has("semester") ? node.get("semester").asText() : "";
            boolean isOpen = node.has("is_open") ? node.get("is_open").asBoolean() : false;
            boolean isLab = node.has("is_lab") ? node.get("is_lab").asBoolean() : false;
            int openSeats = node.has("open_seats") ? node.get("open_seats").asInt() : 0;
            int totalSeats = node.has("total_seats") ? node.get("total_seats").asInt() : 0;
            String description = node.has("description") ? node.get("description").asText() : " ";

            // Faculty is an array, but there doesnt appear to be more than one prof. Named it professor so it's accurate to everything else
            String professor = "TBA";
            JsonNode faculty = node.get("faculty");
            if (faculty != null && !faculty.isEmpty()) {
                professor = faculty.get(0).asText();
            }

            // Made a hashmap for day times
            LinkedHashMap<String, LocalTime[]> dayTimeMap = new LinkedHashMap<>();
            JsonNode times = node.get("times");

            // Parse time as LocalTime object
            if (times != null && !times.isEmpty()) {
                // For each timeslot add it to the day time map
                for (JsonNode t : times) {
                    String day = t.get("day").asText();
                    LocalTime start = LocalTime.parse(t.get("start_time").asText());
                    LocalTime end = LocalTime.parse(t.get("end_time").asText());
                    dayTimeMap.put(day, new LocalTime[]{start, end});
                }
            }

            return new Course(courseCode, courseName, professor, department,
                    location, semester, dayTimeMap, credits, isOpen, isLab, openSeats, totalSeats, description);

        } catch (Exception e) {
            System.err.println("Failed to parse course: " + node.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Course> getCourses() {
        return courses;
    }

}
