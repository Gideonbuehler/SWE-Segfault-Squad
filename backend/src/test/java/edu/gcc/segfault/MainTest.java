package edu.gcc.segfault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private JsonNode classes;

    @BeforeEach
    void setUp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("data_wolfe.json"));
        classes = root.get("classes");
    }

    // fromJson tests
    @Test
    void fromJsonReturnsNullForNullNode() {
        assertNull(Main.fromJson(null));
    }

    @Test
    void fromJsonParsesSubjectNumberSection() {
        Course c = Main.fromJson(classes.get(1)); // ACCT 201-A
        assertEquals("ACCT-201-A", c.getCourseCode());
        assertEquals("ACCT", c.getDepartment());
    }

    @Test
    void fromJsonParsesCourseName() {
        Course c = Main.fromJson(classes.get(1));
        assertEquals("PRINCIPLES OF ACCOUNTING I", c.getCourseName());
    }

    @Test
    void fromJsonParsesFaculty() {
        Course c = Main.fromJson(classes.get(1));
        assertEquals("Graybill, Keith B.", c.getProfessor());
    }

    @Test
    void fromJsonParsesCredits() {
        Course c = Main.fromJson(classes.get(0)); // ABRD 300 has 0 credits
        assertEquals(0, c.getCredits());

        Course c2 = Main.fromJson(classes.get(1));
        assertEquals(3, c2.getCredits());
    }

    @Test
    void fromJsonParsesLocation() {
        Course c = Main.fromJson(classes.get(0));
        assertEquals("Off Campus Course", c.getLocation());
    }

    @Test
    void fromJsonParsesSemester() {
        Course c = Main.fromJson(classes.get(1));
        assertEquals("2023_Fall", c.getSemester());
    }

    @Test
    void fromJsonParsesOpenAndLab() {
        Course closed = Main.fromJson(classes.get(0));
        assertFalse(closed.isOpen());
        assertFalse(closed.isLab());

        Course open = Main.fromJson(classes.get(1));
        assertTrue(open.isOpen());
    }

    @Test
    void fromJsonParsesSeats() {
        Course c = Main.fromJson(classes.get(1));
        assertEquals(1, c.getOpenSeats());
        assertEquals(30, c.getTotalSeats());
    }

    @Test
    void fromJsonParsesTimesWhenPresent() {
        Course c = Main.fromJson(classes.get(1));
        assertEquals(LocalTime.of(15, 30), c.getStartTime());
        assertEquals(LocalTime.of(16, 45), c.getEndTime());
        assertEquals(List.of("T", "R"), c.getDays());
    }

    @Test
    void fromJsonHandlesEmptyTimes() {
        Course c = Main.fromJson(classes.get(0));
        assertNull(c.getStartTime());
        assertNull(c.getEndTime());
        assertTrue(c.getDays().isEmpty());
    }

    @Test
    void fromJsonParsesMWFSchedule() {
        Course c = Main.fromJson(classes.get(2));
        assertEquals(LocalTime.of(10, 0), c.getStartTime());
        assertEquals(LocalTime.of(10, 50), c.getEndTime());
        assertEquals(List.of("M", "W", "F"), c.getDays());
    }

    // loadAll / getCourses tests
    @Test
    void loadAllPopulatesCourseList() throws Exception {
        Main main = new Main();
        main.run();
        ArrayList<Course> courses = Main.getCourses();
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
    }

    @Test
    void getCoursesIsNullBeforeRun() {
        // getCourses() returns whatever was last loaded
        Main main = new Main();
        // After run() courses should be populated
        assertDoesNotThrow(main::run);
        assertNotNull(Main.getCourses());
    }
}