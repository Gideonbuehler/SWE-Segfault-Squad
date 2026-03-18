package edu.gcc.segfault;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
        LocalTime[] l = new LocalTime[2];
        l[0] = LocalTime.of(12, 0);
        l[1] = LocalTime.of(1, 0);
        LinkedHashMap<String, LocalTime[]> m = new LinkedHashMap<>(Map.of("M",l));
        m.put("W", l);
        m.put("F", l);
        LinkedHashMap<String, LocalTime[]> m2 = new LinkedHashMap<>();
        m2.put("W", l);
        LinkedHashMap<String, LocalTime[]> m3 = new LinkedHashMap<>();
        m3.put("T", l);
        s.addCourse(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m, 3, true, false, 20, 30, ""));
        assertFalse(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m2, 1, true, false, 20, 30, "")));
        assertTrue(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m3, 1, true, false, 20, 30, "")));


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
        LocalTime[] l = new LocalTime[2];
        l[0] = LocalTime.of(9, 0);
        l[1] = LocalTime.of(9, 50);
        LinkedHashMap<String, LocalTime[]> m = new LinkedHashMap<>();
        m.put("M", l);
        m.put("W", l);
        m.put("F", l);
        ArrayList<String> days2 = new ArrayList<>();
        days2.add("Tuesday");
        days2.add("Thursday");
        LocalTime[] l2 = new LocalTime[2];
        l2[0] = LocalTime.of(10, 0);
        l2[1] = LocalTime.of(10, 50);

        LinkedHashMap<String, LocalTime[]> m2 = new LinkedHashMap<>();
        m2.put("T", l2);
        m2.put("R", l2);
        Course c1 = new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall",
                m2, 3, true, false, 20, 30, "");
        Course c2 = new Course("code2", "sadness", "Dr. Smith", "COMP", "HAL103", "Fall",
               m, 3, true, false, 20, 30, "");

        // Before adding, no conflicts should exist
        assertTrue(s.checkConflicts(c1));
        assertTrue(s.checkConflicts(c2));

        // After adding c1, a course with the same time/days should conflict
        s.addCourse(c1);
        Course conflictWithC1 = new Course("code3", "conflict", "Dr. Jones", "COMP", "HAL104", "Fall",
                m2, 3, true, false, 20, 30, "");
        assertFalse(s.checkConflicts(conflictWithC1));

        // After adding c2, a course with the same time/days should conflict
        s.addCourse(c2);
        Course conflictWithC2 = new Course("code4", "conflict2", "Dr. Jones", "COMP", "HAL104", "Fall",
                m, 3, true, false, 20, 30, "");
        assertFalse(s.checkConflicts(conflictWithC2));

        // After removing c1, timeslot should be free again
        s.removeCourse(c1);
        assertTrue(s.checkConflicts(conflictWithC1));

        // After removing c2, timeslot should be free again
        s.removeCourse(c2);
        assertTrue(s.checkConflicts(conflictWithC2));
    }

    @Test
    void printScheduletoPDF() throws IOException {
        Schedule s = new Schedule("F25");
        ArrayList<String> days = new ArrayList<>();
        days.add("Monday");
        days.add("Wednesday");
        days.add("Friday");
        ArrayList<String> days2 = new ArrayList<>();
        days2.add("Wednesday");
        ArrayList<String> days3 = new ArrayList<>();
        days3.add("Tuesday");
        LocalTime[] l = new LocalTime[2];
        l[0] = LocalTime.of(12, 0);
        l[1] = LocalTime.of(1, 0);
        LinkedHashMap<String, LocalTime[]> m = new LinkedHashMap<>(Map.of("M",l));
        m.put("W", l);
        m.put("F", l);
        LinkedHashMap<String, LocalTime[]> m2 = new LinkedHashMap<>();
        m2.put("W", l);
        LinkedHashMap<String, LocalTime[]> m3 = new LinkedHashMap<>();
        m3.put("T", l);
        s.addCourse(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m , 3, true, false, 20, 30, ""));
        s.addCourse(new Course("code2", "happiness2", "Dr. Hutchins", "COMP", "HAL102", "Fall",m3, 3, true, false, 20, 30, ""));
        s.makePDF();

    }

    private static final String SAVE_DIR = "schedules/";
    private static final String TEST_SEMESTER = "TEST_SEMESTER";

//    private Course makeCourse(String code, LocalTime start, LocalTime end, ArrayList<String> days) {
//        return new Course(code, "Test Course", "Dr. Test", "COMP", "HAL101",
//                "Fall", start, end, days, 3, true, false, 10, 30);
//    }
//
//    private ArrayList<String> mwfDays() {
//        ArrayList<String> days = new ArrayList<>();
//        days.add("Monday");
//        days.add("Wednesday");
//        days.add("Friday");
//        return days;
//    }
//
//    private ArrayList<String> tthDays() {
//        ArrayList<String> days = new ArrayList<>();
//        days.add("Tuesday");
//        days.add("Thursday");
//        return days;
//    }
//
//    // Delete the test save file before and after each test
//    @BeforeEach
//    @AfterEach
//    void cleanUp() {
//        File f = new File(SAVE_DIR + TEST_SEMESTER + ".json");
//        if (f.exists()) f.delete();
//    }
//
//    @Test
//    void saveSchedule_createsFile() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        s.addCourse(makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays()));
//
//        boolean result = s.saveSchedule();
//
//        assertTrue(result, "saveSchedule() should return true on success");
//        assertTrue(new File(SAVE_DIR + TEST_SEMESTER + ".json").exists(),
//                "Save file should exist after saveSchedule()");
//    }
//
//    @Test
//    void saveSchedule_emptySchedule_createsFile() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//
//        boolean result = s.saveSchedule();
//
//        assertTrue(result, "saveSchedule() should succeed even with no courses");
//        assertTrue(new File(SAVE_DIR + TEST_SEMESTER + ".json").exists());
//    }
//
//    @Test
//    void loadSchedule_noFileExists_returnsFalse() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//
//        boolean result = s.loadSchedule();
//
//        assertFalse(result, "loadSchedule() should return false if no save file exists");
//    }
//
//    @Test
//    void loadSchedule_afterSave_returnsTrue() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        s.addCourse(makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays()));
//        s.saveSchedule();
//
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        assertTrue(loaded.loadSchedule(), "loadSchedule() should return true when file exists");
//    }
//
//
//    @Test
//    void saveAndLoad_singleCourse_preservesCourseCode() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        Course c = makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays());
//        s.addCourse(c);
//        s.saveSchedule();
//
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        loaded.loadSchedule();
//
//        assertEquals(1, loaded.getCourses().size());
//        assertEquals("COMP-101", loaded.getCourses().get(0).getCourseCode());
//    }
//
//    @Test
//    void saveAndLoad_singleCourse_preservesStartAndEndTime() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        Course c = makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays());
//        s.addCourse(c);
//        s.saveSchedule();
//
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        loaded.loadSchedule();
//
//        Course restored = loaded.getCourses().get(0);
//        assertEquals(LocalTime.of(9, 0), restored.getDayTimeMap().firstEntry().getValue()[0]);
//        assertEquals(LocalTime.of(9, 50), restored.getDayTimeMap().firstEntry().getValue()[1]);
//    }
//
//    @Test
//    void saveAndLoad_singleCourse_preservesDays() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        Course c = makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays());
//        s.addCourse(c);
//        s.saveSchedule();
//
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        loaded.loadSchedule();
//
//        assertEquals(mwfDays(), (ArrayList<String>) loaded.getCourses().get(0).getDayTimeMap().keySet());
//    }
//
//    @Test
//    void saveAndLoad_multipleCourses_preservesCount() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        s.addCourse(makeCourse("COMP-101", LocalTime.of(9, 0),  LocalTime.of(9, 50),  mwfDays()));
//        s.addCourse(makeCourse("COMP-201", LocalTime.of(10, 0), LocalTime.of(10, 50), tthDays()));
//        s.saveSchedule();
//
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        loaded.loadSchedule();
//
//        assertEquals(2, loaded.getCourses().size());
//    }
//
//    @Test
//    void saveAndLoad_multipleCourses_conflictStillDetectedAfterLoad() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        s.addCourse(makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays()));
//        s.saveSchedule();
//
//        // Load into a fresh schedule, should be conflict
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        loaded.loadSchedule();
//
//        Course conflict = makeCourse("COMP-999", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays());
//        assertFalse(loaded.checkConflicts(conflict),
//                "Conflict detection should work correctly after loading from file");
//    }
//
//    @Test
//    void saveAndLoad_removeCourse_thenSaveAndReload_courseIsGone() {
//        Schedule s = new Schedule(TEST_SEMESTER);
//        Course c1 = makeCourse("COMP-101", LocalTime.of(9, 0),  LocalTime.of(9, 50),  mwfDays());
//        Course c2 = makeCourse("COMP-201", LocalTime.of(10, 0), LocalTime.of(10, 50), tthDays());
//        s.addCourse(c1);
//        s.addCourse(c2);
//        s.saveSchedule();
//
//        // Remove one course and save again
//        s.removeCourse(c1);
//        s.saveSchedule();
//
//        Schedule loaded = new Schedule(TEST_SEMESTER);
//        loaded.loadSchedule();
//
//        assertEquals(1, loaded.getCourses().size());
//        assertEquals("COMP-201", loaded.getCourses().get(0).getCourseCode());
//    }
//
//
//    @Test
//    void user_setSchedule_autoLoadsExistingSave() {
//        // Create and save a schedule
//        Schedule firstSession = new Schedule(TEST_SEMESTER);
//        firstSession.addCourse(
//                makeCourse("COMP-101", LocalTime.of(9, 0), LocalTime.of(9, 50), mwfDays()));
//        firstSession.saveSchedule();
//
//        // User sets the same schedule name
//        User user = new User();
//        Schedule secondSession = new Schedule(TEST_SEMESTER);
//        user.setSchedule(secondSession); // should auto load
//
//        assertEquals(1, user.getSchedule().getCourses().size(),
//                "User's schedule should be restored from save file on setSchedule()");
//    }
}