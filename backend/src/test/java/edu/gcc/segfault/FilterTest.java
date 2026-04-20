package edu.gcc.segfault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    /** COMP-141-A  MWF 12:00–12:50  3 cr  Wolfe */
    private static final String COMP_141_JSON = """
            {
              "credits": 3,
              "faculty": ["Wolfe, Britton D."],
              "is_lab": false,
              "is_open": false,
              "location": "Science Technology Engineering",
              "name": "COMP PROGRAMMING I",
              "number": 141,
              "open_seats": 0,
              "section": "A",
              "semester": "2023_Fall",
              "subject": "COMP",
              "times": [
                {"day": "M", "start_time": "12:00:00", "end_time": "12:50:00"},
                {"day": "W", "start_time": "12:00:00", "end_time": "12:50:00"},
                {"day": "F", "start_time": "12:00:00", "end_time": "12:50:00"}
              ],
              "total_seats": 32
            }""";

    /** ACCT-201-B  MWF 10:00–10:50  3 cr  Graybill */
    private static final String ACCT_MWF_JSON = """
            {
              "credits": 3,
              "faculty": ["Graybill, Keith B."],
              "is_lab": false,
              "is_open": false,
              "location": "SHAL 309",
              "name": "PRINCIPLES OF ACCOUNTING I",
              "number": 201,
              "open_seats": 0,
              "section": "B",
              "semester": "2023_Fall",
              "subject": "ACCT",
              "times": [
                {"day": "M", "start_time": "10:00:00", "end_time": "10:50:00"},
                {"day": "W", "start_time": "10:00:00", "end_time": "10:50:00"},
                {"day": "F", "start_time": "10:00:00", "end_time": "10:50:00"}
              ],
              "total_seats": 30
            }""";

    /** ACCT-201-A  TR 15:30–16:45  3 cr  Graybill */
    private static final String ACCT_TR_JSON = """
            {
              "credits": 3,
              "faculty": ["Graybill, Keith B."],
              "is_lab": false,
              "is_open": true,
              "location": "SHAL 316",
              "name": "PRINCIPLES OF ACCOUNTING I",
              "number": 201,
              "open_seats": 1,
              "section": "A",
              "semester": "2023_Fall",
              "subject": "ACCT",
              "times": [
                {"day": "T", "start_time": "15:30:00", "end_time": "16:45:00"},
                {"day": "R", "start_time": "15:30:00", "end_time": "16:45:00"}
              ],
              "total_seats": 30
            }""";

    /** ASTR-301-A  T 19:00–21:30  4 cr  Clem */
    private static final String ASTR_301_JSON = """
            {
              "credits": 4,
              "faculty": ["Clem, James L."],
              "is_lab": false,
              "is_open": true,
              "location": "STEM 140B Conference Room",
              "name": "OBSERVATIONAL ASTRONOMY",
              "number": 301,
              "open_seats": 10,
              "section": "A",
              "semester": "2023_Fall",
              "subject": "ASTR",
              "times": [
                {"day": "T", "start_time": "19:00:00", "end_time": "21:30:00"}
              ],
              "total_seats": 16
            }""";

    /** BIOL-332-A  F 14:00–16:59  1 cr  Wood */
    private static final String BIOL_332_JSON = """
            {
              "credits": 1,
              "faculty": ["Wood, Darren"],
              "is_lab": false,
              "is_open": true,
              "location": "STEM 126",
              "name": "ECOLOGY LABORATORY",
              "number": 332,
              "open_seats": 6,
              "section": "A",
              "semester": "2023_Fall",
              "subject": "BIOL",
              "times": [
                {"day": "F", "start_time": "14:00:00", "end_time": "16:59:00"}
              ],
              "total_seats": 24
            }""";

    /** ASTR-301-L  R 19:00–21:00  0 cr  Clem  is_lab=true */
    private static final String ASTR_LAB_JSON = """
            {
              "credits": 0,
              "faculty": ["Clem, James L."],
              "is_lab": true,
              "is_open": true,
              "location": "STEM 140B Conference Room",
              "name": "LABORATORY",
              "number": 301,
              "open_seats": 10,
              "section": "L",
              "semester": "2023_Fall",
              "subject": "ASTR",
              "times": [
                {"day": "R", "start_time": "19:00:00", "end_time": "21:00:00"}
              ],
              "total_seats": 16
            }""";

    /** ABRD-300-A  no times  0 cr  Inman (async / off-campus) */
    private static final String ABRD_300_JSON = """
            {
              "credits": 0,
              "faculty": ["Inman, John G."],
              "is_lab": false,
              "is_open": false,
              "location": "Off Campus Course",
              "name": "STUDY ABROAD",
              "number": 300,
              "open_seats": 0,
              "section": "A",
              "semester": "2023_Fall",
              "subject": "ABRD",
              "times": [],
              "total_seats": 0
            }""";

    private Filter filter;

    private Course compCourse;   // COMP-141-A  MWF 12:00  3 cr  Wolfe
    private Course acctMWF;      // ACCT-201-B  MWF 10:00  3 cr  Graybill
    private Course acctTR;       // ACCT-201-A  TR  15:30  3 cr  Graybill
    private Course astrCourse;   // ASTR-301-A  T   19:00  4 cr  Clem
    private Course biolLab;      // BIOL-332-A  F   14:00  1 cr  Wood
    private Course astrLab;      // ASTR-301-L  R   19:00  0 cr  Clem  (is_lab=true)
    private Course noTimeCourse; // ABRD-300-A  no times    0 cr  Inman

    private Set<Course> allCourses;

    private static Course parse(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        Course c = Main.fromJson(node);
        assertNotNull(c, "Main.fromJson returned null for: " + json);
        return c;
    }

    @BeforeEach
    void setUp() throws Exception {
        filter = new Filter();

        compCourse   = parse(COMP_141_JSON);
        acctMWF      = parse(ACCT_MWF_JSON);
        acctTR       = parse(ACCT_TR_JSON);
        astrCourse   = parse(ASTR_301_JSON);
        biolLab      = parse(BIOL_332_JSON);
        astrLab      = parse(ASTR_LAB_JSON);
        noTimeCourse = parse(ABRD_300_JSON);

        allCourses = new HashSet<>(java.util.List.of(
                compCourse, acctMWF, acctTR, astrCourse, biolLab, astrLab, noTimeCourse));
    }

    @Test
    void noFiltersReturnsAllCourses() {
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "No filters applied should return all courses");
    }

    @Test
    void emptyInputSetReturnsEmpty() {
        assertTrue(filter.applyFilters(new HashSet<>()).isEmpty(),
                "Empty input set should return empty results");
    }

    @Test
    void filterBySingleProfessor() {
        filter.setProfessorNames(new String[]{"Wolfe"});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),  "Should include Wolfe's COMP course");
        assertFalse(result.contains(acctMWF),    "Should exclude Graybill's ACCT-MWF");
        assertFalse(result.contains(acctTR),     "Should exclude Graybill's ACCT-TR");
        assertFalse(result.contains(astrCourse), "Should exclude Clem's ASTR course");
    }

    @Test
    void filterByProfessorMatchesMultipleSections() {
        // Graybill teaches both ACCT-201-A (TR) and ACCT-201-B (MWF)
        filter.setProfessorNames(new String[]{"Graybill"});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(acctMWF), "Should include Graybill's MWF section");
        assertTrue(result.contains(acctTR),  "Should include Graybill's TR section");
        assertFalse(result.contains(compCourse), "Should exclude Wolfe's course");
    }

    @Test
    void filterByMultipleProfessorsORLogic() {
        filter.setProfessorNames(new String[]{"Wolfe", "Clem"});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),   "Should include Wolfe's course");
        assertTrue(result.contains(astrCourse),   "Should include Clem's lecture");
        assertTrue(result.contains(astrLab),      "Should include Clem's lab");
        assertFalse(result.contains(acctMWF),     "Should exclude Graybill's course");
        assertFalse(result.contains(biolLab),     "Should exclude Wood's course");
    }

    @Test
    void filterByProfessorCaseInsensitive() {
        filter.setProfessorNames(new String[]{"wolfe"});
        Set<Course> result = filter.applyFilters(allCourses);
        assertTrue(result.contains(compCourse), "Professor filter should be case-insensitive");
    }

    @Test
    void filterByProfessorPartialLastName() {
        // Real name is "Graybill, Keith B." — filter by last name only
        filter.setProfessorNames(new String[]{"Graybill"});
        Set<Course> result = filter.applyFilters(allCourses);
        assertFalse(result.isEmpty(), "Partial last-name match should return results");
        assertTrue(result.contains(acctMWF));
        assertTrue(result.contains(acctTR));
    }

    @Test
    void filterByProfessorNoMatch() {
        filter.setProfessorNames(new String[]{"NOMATCH"});
        assertTrue(filter.applyFilters(allCourses).isEmpty(),
                "No courses should match a nonexistent professor");
    }

    @Test
    void filterByEmptyProfessorArrayReturnsAll() {
        filter.setProfessorNames(new String[]{});
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "Empty professor array should apply no filter");
    }

    @Test
    void filterBySingleDepartment() {
        // subject field becomes department; here "COMP"
        filter.setDepartmentNames(new String[]{"COMP"});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),   "Should include COMP course");
        assertFalse(result.contains(acctMWF),     "Should exclude ACCT course");
        assertFalse(result.contains(astrCourse),  "Should exclude ASTR course");
    }

    @Test
    void filterByMultipleDepartmentsORLogic() {
        filter.setDepartmentNames(new String[]{"COMP", "ASTR"});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse));
        assertTrue(result.contains(astrCourse));
        assertTrue(result.contains(astrLab));
        assertFalse(result.contains(acctMWF));
        assertFalse(result.contains(biolLab));
    }

    @Test
    void filterByDepartmentNoMatch() {
        filter.setDepartmentNames(new String[]{"PHIL"});
        assertTrue(filter.applyFilters(allCourses).isEmpty(),
                "No courses should match a nonexistent department");
    }

    @Test
    void filterByEmptyDepartmentArrayReturnsAll() {
        filter.setDepartmentNames(new String[]{});
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "Empty department array should apply no filter");
    }

    @Test
    void filterBySingleCreditValue() {
        filter.setCredits(new int[]{3});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),   "3-cr COMP course should be included");
        assertTrue(result.contains(acctMWF),      "3-cr ACCT-MWF should be included");
        assertTrue(result.contains(acctTR),       "3-cr ACCT-TR should be included");
        assertFalse(result.contains(astrCourse),  "4-cr ASTR course should be excluded");
        assertFalse(result.contains(biolLab),     "1-cr BIOL lab should be excluded");
        assertFalse(result.contains(astrLab),     "0-cr lab should be excluded");
    }

    @Test
    void filterByMultipleCreditsORLogic() {
        filter.setCredits(new int[]{3, 4});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse));
        assertTrue(result.contains(acctMWF));
        assertTrue(result.contains(acctTR));
        assertTrue(result.contains(astrCourse));
        assertFalse(result.contains(biolLab),  "1-cr course should be excluded");
        assertFalse(result.contains(astrLab),  "0-cr lab should be excluded");
    }

    @Test
    void filterByCreditsNoMatch() {
        filter.setCredits(new int[]{99});
        assertTrue(filter.applyFilters(allCourses).isEmpty(),
                "No courses should match 99 credits");
    }

    @Test
    void filterByEmptyCreditsArrayReturnsAll() {
        filter.setCredits(new int[]{});
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "Empty credits array should apply no filter");
    }

    @Test
    void filterBySingleDay() {
        filter.setDays(new ArrayList<>(java.util.List.of("M")));
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),   "MWF course includes Monday");
        assertTrue(result.contains(acctMWF),      "MWF ACCT includes Monday");
        assertFalse(result.contains(acctTR),      "TR course does not include Monday");
        assertFalse(result.contains(astrCourse),  "Tuesday-only ASTR does not include Monday");
    }

    @Test
    void filterByMultipleDaysANDLogic() {
        // Course must meet on ALL specified days
        filter.setDays(new ArrayList<>(java.util.List.of("M", "W", "F")));
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),  "MWF COMP matches M+W+F filter");
        assertTrue(result.contains(acctMWF),     "MWF ACCT matches M+W+F filter");
        assertFalse(result.contains(acctTR),     "TR course does not match M+W+F filter");
        assertFalse(result.contains(astrCourse), "T-only course does not match M+W+F filter");
    }

    @Test
    void filterByDayNoMatch() {
        filter.setDays(new ArrayList<>(java.util.List.of("S")));
        assertTrue(filter.applyFilters(allCourses).isEmpty(),
                "No courses meet on Saturday");
    }

    @Test
    void filterByEmptyDaysReturnsAll() {
        filter.setDays(new ArrayList<>());
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "Empty days filter should return all courses");
    }

    @Test
    void filterByStartTime() {
        // 15:30 → should include anything from 15:30 and later
        filter.setStartTimes(new LocalTime[]{LocalTime.of(15, 30)});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(acctTR),      "15:30 exact start should be included");
        assertFalse(result.contains(compCourse), "12:00 start should be excluded");
        assertFalse(result.contains(acctMWF),    "10:00 start should be excluded");
        assertTrue(result.contains(astrCourse), "19:00, after start(15:30) should be included");
        assertFalse(result.contains(biolLab),    "14:00 start should be excluded");
    }

    @Test
    void filterByStartTimeExactMatch() {
        filter.setStartTimes(new LocalTime[]{LocalTime.of(12, 0)});
        Set<Course> result = filter.applyFilters(allCourses);
        assertTrue(result.contains(compCourse),  "Exact start time 12:00 should be included");
        assertFalse(result.contains(acctMWF),    "10:00 start should not match 12:00 filter");
    }

    @Test
    void filterByStartTimeNullTimeCourseExcluded() {
        filter.setStartTimes(new LocalTime[]{LocalTime.of(0, 0)});
        Set<Course> result = filter.applyFilters(allCourses);
        assertFalse(result.contains(noTimeCourse),
                "Course with null start time should be excluded when start filter is active");
    }

    @Test
    void filterByEmptyStartTimeReturnsAll() {
        filter.setStartTimes(new LocalTime[]{});
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "Empty start time array should apply no filter");
    }

    @Test
    void filterByEndTime() {
        filter.setEndTimes(new LocalTime[]{LocalTime.of(12, 50)});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(compCourse),   "Ends exactly 12:50, should be included");
        assertTrue(result.contains(acctMWF),     "Ends 10:50, which is before 12:50, should be included");
        assertFalse(result.contains(acctTR),      "Ends 16:45, should be excluded");
        assertFalse(result.contains(astrCourse),  "Ends 21:30, should be excluded");
        assertFalse(result.contains(biolLab),     "Ends 16:59, should be excluded");
    }

    @Test
    void filterByEndTimeExactMatch() {
        filter.setEndTimes(new LocalTime[]{LocalTime.of(10, 50)});
        Set<Course> result = filter.applyFilters(allCourses);
        assertTrue(result.contains(acctMWF),     "Exact end time 10:50 should be included");
        assertFalse(result.contains(compCourse), "12:50 end should not match 10:50 filter");
    }

    @Test
    void filterByEndTimeNullTimeCourseExcluded() {
        filter.setEndTimes(new LocalTime[]{LocalTime.of(23, 59)});
        Set<Course> result = filter.applyFilters(allCourses);
        assertFalse(result.contains(noTimeCourse),
                "Course with null end time should be excluded when end filter is active");
    }

    @Test
    void filterByEmptyEndTimeReturnsAll() {
        filter.setEndTimes(new LocalTime[]{});
        assertEquals(allCourses, filter.applyFilters(allCourses),
                "Empty end time array should apply no filter");
    }

    @Test
    void filterByStartAndEndTimeExact() {
        // Exact start 10:00 AND exact end 10:50 → only acctMWF
        filter.setStartTimes(new LocalTime[]{LocalTime.of(10, 0)});
        filter.setEndTimes(new LocalTime[]{LocalTime.of(10, 50)});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(acctMWF),     "10:00-10:50 exact match");
        assertFalse(result.contains(compCourse), "12:00-12:50 should not match");
        assertFalse(result.contains(acctTR),     "15:30-16:45 should not match");
    }

    @Test
    void filterByDepartmentAndCredits() {
        filter.setDepartmentNames(new String[]{"ASTR"});
        filter.setCredits(new int[]{4});
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(astrCourse),  "4-cr ASTR lecture should be included");
        assertFalse(result.contains(astrLab),    "0-cr ASTR lab excluded by credits filter");
        assertFalse(result.contains(compCourse), "COMP excluded by department filter");
    }

    @Test
    void filterByProfessorAndDays() {
        // Clem teaches both astrCourse (T) and astrLab (R); filter to T only
        filter.setProfessorNames(new String[]{"Clem"});
        filter.setDays(new ArrayList<>(java.util.List.of("T")));
        Set<Course> result = filter.applyFilters(allCourses);

        assertTrue(result.contains(astrCourse), "Clem's T course should be included");
        assertFalse(result.contains(astrLab),   "Clem's R-only lab should be excluded");
    }

    @Test
    void allFiltersCombined() {
        // Only compCourse (COMP, 3 cr, Wolfe, MWF, starts 12:00, ends 12:50) survives
        filter.setDepartmentNames(new String[]{"COMP"});
        filter.setCredits(new int[]{3});
        filter.setProfessorNames(new String[]{"Wolfe"});
        filter.setDays(new ArrayList<>(java.util.List.of("M", "W", "F")));
        filter.setStartTimes(new LocalTime[]{LocalTime.of(12, 0)});
        filter.setEndTimes(new LocalTime[]{LocalTime.of(12, 50)});

        assertEquals(Set.of(compCourse), filter.applyFilters(allCourses),
                "Only compCourse should survive all filters");
    }

    @Test
    void filterDoesNotMutateOriginalSet() {
        Set<Course> snapshot = new HashSet<>(allCourses);
        filter.setDepartmentNames(new String[]{"ASTR"});
        filter.applyFilters(allCourses);
        assertEquals(snapshot, allCourses, "applyFilters must not modify the original set");
    }

    @Test
    void labFlagDoesNotAffectOtherFilters() {
        // astrLab has is_lab=true; filtering by department should still include it
        filter.setDepartmentNames(new String[]{"ASTR"});
        Set<Course> result = filter.applyFilters(allCourses);
        assertTrue(result.contains(astrLab), "Lab course should still appear in department filter results");
    }

    @Test
    void courseWithNoTimesIncludedWhenNoTimeFilterSet() {
        // noTimeCourse has null start/end — it should still appear when no time filter is active
        Set<Course> result = filter.applyFilters(allCourses);
        assertTrue(result.contains(noTimeCourse),
                "Async/no-time course should be included when no time filter is applied");
    }

    @Test
    void sameSubjectDifferentSectionsFilteredTogether() {
        // ACCT-201-A and ACCT-201-B are the same subject — both should appear for department "ACCT"
        filter.setDepartmentNames(new String[]{"ACCT"});
        Set<Course> result = filter.applyFilters(allCourses);
        assertTrue(result.contains(acctMWF));
        assertTrue(result.contains(acctTR));
        assertEquals(2, result.size(), "Only the two ACCT sections should be returned");
    }
}

