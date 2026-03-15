package edu.gcc.segfault;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ProfileTest {

    @Test
    void updateYear() {
        Profile p = new Profile("Freshman", "computer Science", new ArrayList<>(List.of("History")), new ArrayList<>());
        assertTrue(p.updateYear("sophomore"));

    }

    @Test
    void updateMajor() {
        Profile p = new Profile("Freshman", "computer Science", new ArrayList<>(List.of("History")), new ArrayList<>());
        assertTrue(p.updateMajor("Computer Programming"));
    }

    @Test
    void addMinors() {
        Profile p = new Profile("Freshman", "computer Science", new ArrayList<>(List.of("History")), new ArrayList<>());
        assertTrue(p.addMinor("Computer Game Design and Development"));
    }

    @Test
    void removeMinor() {
        Profile p = new Profile("Freshman", "computer Science", new ArrayList<>(List.of("History")), new ArrayList<>());
        assertTrue(p.deleteMinor("History"));
    }

    @Test
    void addCompletedCourses() {
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");
        Profile p = new Profile("Freshman", "computer Science", new ArrayList<>(List.of("History")), new ArrayList<>());
        assertTrue(p.addCompletedCourses(new ArrayList<>(List.of(new Course("code1",
                "happiness", "Dr. Hutchins", "COMP",
                "HAL102", "Fall", LocalTime.of(12, 0),
                LocalTime.of(12, 50), days , 3, true,
                false, 20, 30)))));
    }

    @Test
    void removeCompletedCourses() {
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");
        Profile p = new Profile("Freshman", "computer Science", new ArrayList<>(List.of("History")), new ArrayList<>());
        p.addCompletedCourses(new ArrayList<>(List.of(new Course("code1", "happiness",
                "Dr. Hutchins", "COMP", "HAL102", "Fall",
                LocalTime.of(12, 0), LocalTime.of(12, 50), days , 3,
                true, false, 20, 30))));

        assertTrue(p.removeCompletedCourses(new ArrayList<>(List.of(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall",
                LocalTime.of(12, 0), LocalTime.of(12, 50), days ,
                3, true, false, 20, 30)))));
    }
}