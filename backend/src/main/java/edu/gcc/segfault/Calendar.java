package edu.gcc.segfault;

import java.util.ArrayList;

public class Calendar {

    private ArrayList<Block> blocks;


    public Calendar() {
        blocks = new ArrayList<Block>();
    }


    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public boolean addTimeBlock(Course course){
        Block newBlock = new Block();

        newBlock.setCourse(course);
        newBlock.setStartTime(course.getStartTime());
        newBlock.setEndTime(course.getEndTime());

        return blocks.add(newBlock);
    }

    public boolean removeTimeBlock(Course course){

        Block toRemove = new Block(course);
        return blocks.remove(toRemove);
    }

    public void display(){

    }
}
