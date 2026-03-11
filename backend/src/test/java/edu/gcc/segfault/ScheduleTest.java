package edu.gcc.segfault;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleTest {

    @Test
    void checkConflicts() {
        LocalTime stime = LocalTime.of(12, 00);
        Schedule s = new Schedule("F25");
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");
        ArrayList<String> days2 = new ArrayList<>();
        days2.add("Wednesday");
        ArrayList<String> days3 = new ArrayList<>();
        days3.add("Tuesday");
        s.addCourse(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days , 3, true, false, 20, 30));
        assertFalse(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days2, 1, true, false, 20, 30)));
        assertTrue(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days3, 1, true, false, 20, 30)));


    }

    @Test
    void getCourses() {
    }

    @Test
    void addAndRemoveCourses() {
        Schedule s = new Schedule("F25");
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");

        ArrayList<String> days2 = new ArrayList<>();
        days2.add("Tuesday");
        days2.add("Thursday");

        Course c1 = new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall",
                LocalTime.of(9, 0), LocalTime.of(9, 50), days, 3, true, false, 20, 30);
        Course c2 = new Course("code2", "sadness", "Dr. Smith", "COMP", "HAL103", "Fall",
                LocalTime.of(10, 0), LocalTime.of(10, 50), days2, 3, true, false, 20, 30);

        // Before adding, no conflicts should exist
        assertTrue(s.checkConflicts(c1));
        assertTrue(s.checkConflicts(c2));

        // After adding c1, a course with the same time/days should conflict
        s.addCourse(c1);
        Course conflictWithC1 = new Course("code3", "conflict", "Dr. Jones", "COMP", "HAL104", "Fall",
                LocalTime.of(9, 0), LocalTime.of(9, 50), days, 3, true, false, 20, 30);
        assertFalse(s.checkConflicts(conflictWithC1));

        // After adding c2, a course with the same time/days should conflict
        s.addCourse(c2);
        Course conflictWithC2 = new Course("code4", "conflict2", "Dr. Jones", "COMP", "HAL104", "Fall",
                LocalTime.of(10, 0), LocalTime.of(10, 50), days2, 3, true, false, 20, 30);
        assertFalse(s.checkConflicts(conflictWithC2));

        // After removing c1, timeslot should be free again
        s.removeCourse(c1);
        assertTrue(s.checkConflicts(conflictWithC1));

        // After removing c2, timeslot should be free again
        s.removeCourse(c2);
        assertTrue(s.checkConflicts(conflictWithC2));
    }
}