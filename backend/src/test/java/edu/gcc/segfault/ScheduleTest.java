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
}