package edu.gcc.segfault;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {

    @Test
    void addTimeBlock() {

        // Arrange
        Calendar c = new Calendar();

        int initialLength = c.getBlocks().size();

        ArrayList<String> days = new ArrayList<>(Arrays.asList("Mon", "Wed"));
        LocalTime[] l = new LocalTime[2];
        l[0] = LocalTime.of(9, 0);
        l[1] = LocalTime.of(10, 0);
        LinkedHashMap<String, LocalTime[]> m = new LinkedHashMap<>(Map.of("M", l));
        Course course = new Course(
                "CS101",
                "Intro to CS",
                "Dr. Smith",
                "Computer Science",
                "Room 101",
                "Fall 2026",
                m,
                3,
                true,
                false,
                10,
                30
        );
        Block block = new Block(course);

        // Act
        c.addTimeBlock(course);

        // Assert
        assertEquals(initialLength + 1, c.getBlocks().size());
        assertTrue(c.getBlocks().contains(block));
        assertEquals(block, c.getBlocks().getFirst());
    }

//    @Test
//    void testRemoveTimeBlock() {
//
//        // Arrange
//        Calendar c = new Calendar();
//
//        ArrayList<String> days = new ArrayList<>(Arrays.asList("Mon", "Wed"));
//        LinkedHashMap<String, LocalTime[]> m2 = new LinkedHashMap<>();
//        LocalTime[] l2 = new LocalTime[2];
//        l2[0] = LocalTime.of(9, 0);
//        l2[1] = LocalTime.of(10, 0);
//        m2.put("M", l2);
//        m2.put("W", l2);
//
//        Course course1 = new Course(
//                "CS101", "Intro to CS", "Dr. Smith", "CS",
//                "Room 101", "Fall 2026",
//
//
//                m2, 3, true, false, 10, 30
//        );
//
//        Course course2 = new Course(
//                "MATH201", "Calculus II", "Dr. Johnson", "Math",
//                "Room 202", "Fall 2026",
//                LocalTime.of(11, 0),
//                LocalTime.of(12, 0),
//                days, 4, true, false, 5, 25
//        );
//
//        Course course3 = new Course(
//                "ENG150", "English Lit", "Dr. Brown", "English",
//                "Room 303", "Fall 2026",
//                LocalTime.of(13, 0),
//                LocalTime.of(14, 0),
//                days, 3, true, false, 8, 20
//        );
//
//        Block block1 = new Block(course1);
//        Block block2 = new Block(course2);
//        Block block3 = new Block(course3);
//
//        c.addTimeBlock(course1);
//        c.addTimeBlock(course2);
//        c.addTimeBlock(course3);
//
//        assertEquals(3, c.getBlocks().size());
//
//        // Act
//        c.removeTimeBlock(course2);
//
//        // Assert
//        assertEquals(2, c.getBlocks().size());
//
//        assertFalse(c.getBlocks().contains(block2));
//        assertTrue(c.getBlocks().contains(block1));
//        assertTrue(c.getBlocks().contains(block3));
//    }

    @Test
    void display() {
    }
}