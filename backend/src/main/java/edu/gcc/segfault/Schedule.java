package edu.gcc.segfault;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;


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

    public Schedule(String name){
        semesterName = name;
        courses = new ArrayList<>();
        calendar = new Calendar();
    }
    public void addCourse(Course toAdd){
        if(checkConflicts(toAdd)) {
            courses.add(toAdd);
        }
    }

    public void removeCourse(Course toRemove){

    }

    public boolean checkConflicts(Course toCheck){
        for(Course c: courses){
            for(String d: c.getDays()){
                for(String d2: toCheck.getDays()){
                    System.out.println(d + " " + d2);
                    if(d.equals(d2)){
                        //if the start time is in the time of the other classes
                        if(!c.getStartTime().isAfter(toCheck.getStartTime()) && c.getEndTime().isAfter(toCheck.getStartTime())){
                            return false;
                        }
                        //if the end time is in the time of another class
                        if(c.getStartTime().isBefore(toCheck.getEndTime()) && c.getEndTime().isAfter(toCheck.getEndTime())){
                            return false;
                        }
                        //check if there is overlap on the specific end/beginning times
                        if(c.getStartTime().equals(toCheck.getStartTime()) || c.getEndTime().equals(toCheck.getStartTime()) ||
                        c.getEndTime().equals(toCheck.getEndTime()) || c.getStartTime().equals(toCheck.getEndTime())){
                            return false;
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
        s.addCourse(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days , 3, true, false, 20, 30));
        System.out.println(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days2, 1, true, false, 20, 30)));
        System.out.println(s.checkConflicts(new Course("code1", "happiness", "Dr. Hutchins", "COMP", "HAL102", "Fall", LocalTime.of(12, 0), LocalTime.of(12, 50), days3, 1, true, false, 20, 30)));

    }

    public boolean saveSchedule(){
        return false;
    }

    public boolean loadSchedule(){
        return false;
    }

    public ArrayList<Course> getCourses(){
        return new ArrayList<>();
    }

    public void makePDF() throws IOException {
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
        cell = header.createCell(12f, "Days");
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(PDType1Font.COURIER_BOLD);
        cell.setFontSize(12);
        cell = header.createCell(12f, "Times");
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
            newRow.createCell(16f, "We need to discover this");
            newRow.createCell(12f, c.getDays().toString());
            newRow.createCell(12f, c.getStartTime() + "-" + c.getEndTime());
            newRow.createCell(12f, c.getProfessor());
            newRow.createCell(12f, c.getCredits() + "");
        }
        table.draw();
        write.endText();
        write.close();
        pdf.save("Schedule.pdf");
    }
}
