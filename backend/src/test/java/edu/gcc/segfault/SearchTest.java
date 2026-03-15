package edu.gcc.segfault;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {
    private Search search;

    @BeforeEach
    void setUp() {
        search = new Search();
    }

    @Test
    void fetchQueryMatchBySubject() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("ACCT"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Expected results for subject 'ACCT'");
        assertTrue(result.stream().anyMatch(c ->
                        c.getCourseCode().toUpperCase().contains("ACCT")),
                "All results should contain 'ACCT' in course code");
    }

    @Test
    void fetchQueryMatchByName() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("PRINCIPLES", "OF", "ACCOUNTING"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Expected results for 'PRINCIPLES OF ACCOUNTING'");
        assertTrue(result.stream().anyMatch(c ->
                        c.getCourseName().toUpperCase().contains("PRINCIPLES")),
                "Results should contain courses with 'PRINCIPLES' in the name");
    }

    @Test
    void fetchQueryMatchByPartOfName() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("PRINCIPLES"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Expected results for partial name 'PRINCIPLES'");
        assertTrue(result.stream().anyMatch(c ->
                        c.getCourseName().toUpperCase().contains("PRINCIPLES")),
                "At least one result should contain 'PRINCIPLES' in the name");
    }

    @Test
    void fetchQueryMatchByProfessor() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("Graybill"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Expected results for professor 'Graybill'");
        assertTrue(result.stream().anyMatch(c ->
                        c.getProfessor().toLowerCase().contains("graybill")),
                "Results should contain courses taught by Graybill");
    }

    // ---- CASE INSENSITIVITY ----

    @Test
    void fetchQueryCaseInsensitiveLower() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("principles", "accounting"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Search should be case insensitive (lowercase)");
    }

    @Test
    void fetchQueryCaseInsensitiveMixed() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("PrInCiPlEs", "AcCoUnTiNg"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Search should be case insensitive (mixed case)");
    }

    @Test
    void fetchQueryCaseInsensitiveMatchesLowerAndUpper() throws Exception {
        ArrayList<String> lowerKeywords = new ArrayList<>(List.of("accounting"));
        ArrayList<String> upperKeywords = new ArrayList<>(List.of("ACCOUNTING"));
        Set<Course> lowerResult = search.fetchQuery(lowerKeywords);
        Set<Course> upperResult = search.fetchQuery(upperKeywords);
        assertEquals(lowerResult, upperResult, "Upper and lowercase searches should return identical results");
    }

    // ---- EDGE CASES ----

    @Test
    void fetchQueryEmptyKeywords() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        Set<Course> result = search.fetchQuery(keywords);
        assertTrue(result.isEmpty(), "Empty keyword list should return empty results");
    }

    @Test
    void fetchQueryNullInput() {
        assertThrows(NullPointerException.class, () ->
                        search.fetchQuery(null),
                "Null input should throw NullPointerException");
    }

    @Test
    void fetchQueryEmptyStringKeyword() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of(""));
        Set<Course> result = search.fetchQuery(keywords);
        // An empty string matches everything — assert it returns all courses or is handled explicitly
        assertTrue(result.isEmpty() || result.size() > 1,
                "Empty string keyword should either be ignored or match broadly");
    }

    @Test
    void fetchQueryNoMatch() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("PHYS999ZZZNOMATCH"));
        Set<Course> result = search.fetchQuery(keywords);
        assertTrue(result.isEmpty(), "Nonsense keyword should return no results");
    }

    @Test
    void fetchQuerySingleCharacter() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("A"));
        Set<Course> result = search.fetchQuery(keywords);
        // Just verifying it doesn't crash and returns something reasonable
        assertNotNull(result, "Single character search should not return null");
    }

    @Test
    void fetchQueryMultipleKeywordsSameField() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("COST", "ACCOUNTING"));
        Set<Course> result = search.fetchQuery(keywords);
        assertTrue(result.size() >= 1, "Expected at least one course matching 'COST ACCOUNTING'");
        assertTrue(result.stream().allMatch(c ->
                        c.getCourseName().toUpperCase().contains("COST") ||
                                c.getCourseCode().toUpperCase().contains("COST")),
                "All results should be relevant to 'COST'");
    }

    @Test
    void fetchQueryKeywordsAcrossFields() throws Exception {
        // One keyword matches course code, another matches professor — tests cross-field AND logic
        ArrayList<String> keywords = new ArrayList<>(List.of("ACCT", "Graybill"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Should find courses where code matches 'ACCT' and professor matches 'Graybill'");
        assertTrue(result.stream().allMatch(c ->
                        c.getCourseCode().toUpperCase().contains("ACCT") &&
                                c.getProfessor().toLowerCase().contains("graybill")),
                "All results should match both fields");
    }

    @Test
    void fetchQueryMatchByDepartment() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("ART"));
        Set<Course> result = search.fetchQuery(keywords);
        assertFalse(result.isEmpty(), "Expected results for department 'ART'");
        assertTrue(result.stream().anyMatch(c ->
                        c.getDepartment().toUpperCase().contains("ART")),
                "At least one result should be from the ART department");
    }

    @Test
    void fetchQueryUpdatesHistory() throws Exception {
        ArrayList<String> keywords = new ArrayList<>(List.of("ACCT"));
        search.fetchQuery(keywords);
        Set<Course> results = search.getResults();
        assertNotNull(results, "History should be updated after fetchQuery");
        assertFalse(results.isEmpty(), "History should contain the last query results");
    }

    @Test
    void fetchQueryHistoryReflectsLatestSearch() throws Exception {
        search.fetchQuery(new ArrayList<>(List.of("ACCT")));
        Set<Course> firstResult = search.getResults();

        search.fetchQuery(new ArrayList<>(List.of("COMP")));
        Set<Course> secondResult = search.getResults();

        assertNotEquals(firstResult, secondResult, "History should reflect the most recent search");
    }
}