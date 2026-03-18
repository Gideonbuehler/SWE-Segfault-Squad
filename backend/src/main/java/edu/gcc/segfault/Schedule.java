package edu.gcc.segfault;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.*;


import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import net.bytebuddy.asm.Advice;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.VerticalAlignment;

public class Schedule {
    private String semesterName;
    private ArrayList<Course> courses;
    private Calendar calendar;
    private PDDocument pdf;

    //File path to store/save schedule
    private static final String save_dir = "schedules/";

    public Schedule(String name){
        semesterName = name;
        courses = new ArrayList<>();
        calendar = new Calendar();
    }
    public boolean addCourse(Course toAdd){
        if(checkConflicts(toAdd)) {
            courses.add(toAdd);
            calendar.addTimeBlock(toAdd);
            System.out.println(courses.toString());
            saveSchedule();
            return true;
        }

        return false;
    }

    public void removeCourse(Course toRemove){
        courses.remove(toRemove);
        calendar.removeTimeBlock(toRemove);
        System.out.println(courses.toString());
        saveSchedule();
    }

    public boolean checkConflicts(Course toCheck){
        if(!courses.isEmpty()) {
            for (Course c : courses) {
                for (Map.Entry<String, LocalTime[]> d : c.getDayTimeMap().entrySet()) {
                    for (Map.Entry<String, LocalTime[]> d2 : toCheck.getDayTimeMap().entrySet()) {
                        System.out.println(d + " " + d2);
                        if (d.getKey().equals(d2.getKey())) {
                            //if the start time is in the time of the other classes
                            if (!d.getValue()[0].isAfter(d2.getValue()[0]) && d.getValue()[1].isAfter(d2.getValue()[0])) {
                                return false;
                            }
                            //if the end time is in the time of another class
                            if (d.getValue()[0].isBefore(d2.getValue()[1]) && d.getValue()[1].isAfter(d2.getValue()[1])) {
                                return false;
                            }
                            //check if there is overlap on the specific end/beginning times
                            if (d.getValue()[0].equals(d2.getValue()[0]) || d.getValue()[1].equals(d2.getValue()[0]) ||
                                    d.getValue()[1].equals(d2.getValue()[1]) || d.getValue()[0].equals(d2.getValue()[1])) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        //this is my 5th test

        return true;
    }

    public static void main(String[] args) {
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
        s.addCourse(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m , 3, true, false, 20, 30, "This is the first course that is cool"));
        System.out.println(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m2, 1, true, false, 20, 30, " ")));
        System.out.println(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", m3, 1, true, false, 20, 30, "This is the second course that is awesome")));

    }

    public boolean saveSchedule(){
        try {
            // Make sure the save directory exists
            File dir = new File(save_dir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // handles LocalTime
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            // Creates/writes to save file
            File saveFile = new File(save_dir + semesterName + ".json");
            mapper.writeValue(saveFile, courses);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean loadSchedule(){
        try {
            File saveFile = new File(save_dir + semesterName + ".json");
            if (!saveFile.exists()) {
                return false;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Tells Jackson that it wants an arraylist of Courses
            ArrayList<Course> loaded = mapper.readValue(
                    saveFile,
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Course.class)
            );

            // Clear current state and rebuild from loaded data
            courses.clear();
            calendar = new Calendar();
            for (Course c : loaded) {
                courses.add(c);
                calendar.addTimeBlock(c);
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<Course> getCourses(){
        return new ArrayList<>(courses);
    }
    public Calendar getCalendar(){
        return calendar;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Course c : courses) {
            s.append(c.toString()).append(" ");
        }

        return s.toString();
    }

    public void makePDF() throws IOException {
        //Need to push
        pdf = new PDDocument();
        PDPage schedulePage = new PDPage(PDRectangle.A4);
        pdf.addPage(schedulePage);
        PDPageContentStream write = new PDPageContentStream(pdf, schedulePage);
        float margin = 50;
        float yPosition = PDRectangle.A4.getWidth() - margin;
        BaseTable table = new BaseTable(yPosition, yPosition, margin, PDRectangle.A4.getWidth(), margin, pdf, schedulePage, true, true);
        Row<PDPage> header = table.createRow(33f);

        Cell<PDPage> cell = header.createCell(12f, "CODE");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(12);
        cell = header.createCell(12f, "Course Title");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(12);
        cell = header.createCell(16f, "Description");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(12);
        //Combine the days and times?
        cell = header.createCell(24f, "Days/Times");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(12);
        cell = header.createCell(12f, "Professor");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(10);
        cell = header.createCell(12f, "Credit Hours");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(12);



        write.beginText();
        write.setFont(PDType1Font.COURIER, 24);
        for(Course c: courses){
            Row<PDPage> newRow = table.createRow(33f);
            newRow.createCell(12f, c.getCourseCode());
            newRow.createCell(12f, c.getCourseName());
            String description = c.getDescription();
            if(description.isEmpty()){
                description = "We need to discover this";
            }
            else if(description.length() > 24){
                description = description.substring(0, 24);
            }
            newRow.createCell(16f, description);
            Cell<PDPage> cell1 = newRow.createCell(24f, "");
            for(Map.Entry<String, LocalTime[]> s : c.getDayTimeMap().entrySet()) {
                if(s.equals(c.getDayTimeMap().lastEntry())){
                    cell1.setText(cell1.getText() + s.getKey() + " " + s.getValue()[0] + "-" + s.getValue()[1]);
                }
                else {
                    cell1.setText(cell1.getText() + s.getKey() + " " + s.getValue()[0] + "-" + s.getValue()[1] + ", ");
                }
            }
            newRow.createCell(12f, c.getProfessor());
            newRow.createCell(12f, c.getCredits() + "");
        }
        table.draw();
        write.endText();
        write.close();
        pdf.save("Schedule.pdf");
    }
}
