package edu.gcc.segfault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    private Course courseWithTimes;
    private Course courseWithoutTimes;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("data_wolfe.json"));
        JsonNode classes = root.get("classes");

        // First course (ABRD 300-A) has no times
        courseWithoutTimes = Main.fromJson(classes.get(0));
        assertNotNull(courseWithoutTimes);

        // Second course (ACCT 201-A) has times
        courseWithTimes = Main.fromJson(classes.get(1));
        assertNotNull(courseWithTimes);
    }

    @Test
    void getCourseCode() {
        assertEquals("ABRD-300-A", courseWithoutTimes.getCourseCode());
        assertEquals("ACCT-201-A", courseWithTimes.getCourseCode());
    }

    @Test
    void getCourseName() {
        assertEquals("STUDY ABROAD", courseWithoutTimes.getCourseName());
        assertEquals("PRINCIPLES OF ACCOUNTING I", courseWithTimes.getCourseName());
    }

    @Test
    void getProfessor() {
        assertEquals("Inman, John G.", courseWithoutTimes.getProfessor());
        assertEquals("Graybill, Keith B.", courseWithTimes.getProfessor());
    }

    @Test
    void getDepartment() {
        assertEquals("ABRD", courseWithoutTimes.getDepartment());
        assertEquals("ACCT", courseWithTimes.getDepartment());
    }

    @Test
    void getStartTime() {
        assertNull(courseWithoutTimes.getStartTime());
        assertEquals(LocalTime.of(15, 30), courseWithTimes.getStartTime());
    }

    @Test
    void getEndTime() {
        assertNull(courseWithoutTimes.getEndTime());
        assertEquals(LocalTime.of(16, 45), courseWithTimes.getEndTime());
    }

    @Test
    void getDays() {
        assertTrue(courseWithoutTimes.getDays().isEmpty());
        assertEquals(List.of("T", "R"), courseWithTimes.getDays());
    }

    @Test
    void getCredits() {
        assertEquals(0, courseWithoutTimes.getCredits());
        assertEquals(3, courseWithTimes.getCredits());
    }

    @Test
    void getLocation() {
        assertEquals("Off Campus Course", courseWithoutTimes.getLocation());
        assertEquals("SHAL 316", courseWithTimes.getLocation());
    }

    @Test
    void isOpen() {
        assertFalse(courseWithoutTimes.isOpen());
        assertTrue(courseWithTimes.isOpen());
    }

    @Test
    void isLab() {
        assertFalse(courseWithoutTimes.isLab());
        assertFalse(courseWithTimes.isLab());
    }

    @Test
    void getOpenSeats() {
        assertEquals(0, courseWithoutTimes.getOpenSeats());
        assertEquals(1, courseWithTimes.getOpenSeats());
    }

    @Test
    void getTotalSeats() {
        assertEquals(0, courseWithoutTimes.getTotalSeats());
        assertEquals(30, courseWithTimes.getTotalSeats());
    }

    @Test
    void getSemester() {
        assertEquals("2023_Fall", courseWithoutTimes.getSemester());
        assertEquals("2023_Fall", courseWithTimes.getSemester());
    }
}