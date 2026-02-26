package edu.gcc.segfault;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SearchTest {

    @Test
    void fetchQueryMatchBySubject() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("ACCT");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertFalse(result.isEmpty());
    }

    @Test
    void fetchQueryMatchByName() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("PRINCIPLES OF ACCOUNTING I");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertFalse(result.isEmpty());
    }

    @Test
    void fetchQueryMatchByProfessor() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("Graybill, Keith B.");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertFalse(result.isEmpty());
    }

    @Test
    void fetchQueryCaseInsensitive() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("principles of accounting i");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertFalse(result.isEmpty());
    }

    @Test
    void fetchQueryNoMatch() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("PHYS999");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertTrue(result.isEmpty());
    }

    @Test
    void fetchQueryMultipleKeywords() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("COST ACCOUNTING");
        keywords.add("AUDITING");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertTrue(result.size() >= 2);
    }

    @Test
    void fetchQueryEmptyKeywords() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertTrue(result.isEmpty());
    }

    @Test
    void fetchQueryMatchByDepartment() throws Exception {
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("ART");
        ArrayList<Course> result = new Search().fetchQuery(keywords);
        assertFalse(result.isEmpty());
    }
}