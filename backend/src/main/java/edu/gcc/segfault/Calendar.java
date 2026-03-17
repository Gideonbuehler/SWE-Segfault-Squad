package edu.gcc.segfault;

import java.util.ArrayList;

public class Calendar {

    // The blocks representing courses in the Calendar
    private ArrayList<Block> blocks;

    // Default
    public Calendar() {
        blocks = new ArrayList<Block>();
    }


    public ArrayList<Block> getBlocks() {
        return blocks;
    }


    public boolean addTimeBlock(Course course){
        Block newBlock = new Block();

        newBlock.setCourse(course);
        newBlock.setStartTime(course.getDayTimeMap().firstEntry().getValue()[0]);
        newBlock.setEndTime(course.getDayTimeMap().firstEntry().getValue()[1]);

        return blocks.add(newBlock);
    }

    public boolean removeTimeBlock(Course course){

        Block toRemove = new Block(course);
        return blocks.remove(toRemove);
    }

    public void display(){

    }
}
