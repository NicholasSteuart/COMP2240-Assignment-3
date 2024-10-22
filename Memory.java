/* File: Memory.java
* Author: Nicholas Steuart c3330826
* Date Created: 14/10/24
* Date Last Modified: 14/10/24
* Description: Simulates main memory in a computing environment. 
* Contains implementation of the 2 LRU Replacement algorithms required by the assignment
*/

// PACKAGES //

import java.util.ArrayList;

public class Memory 
{
    // CLASS VARIABLES //

    private ArrayList<Frame> frames;    //The main memory
    private int totalFrames;            //The total amount of Frames in main memory

    // CONSTRUCTORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Memory()
    {
        frames = null;
        totalFrames = 0;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Memory(int totalFrames)
    {
        this.totalFrames = totalFrames;
        frames = new ArrayList<>(totalFrames);
    }

    // METHODS //
    
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public void allocateMemory(ArrayList<Process> processes, boolean isGlobal)
    {   
        //The total amount of Frames allocatable to a Process based on scope 
        int maxFrames = (!isGlobal) ? totalFrames / processes.size() : totalFrames;

        //Assign Frames to Processes basd on scope
        if(!isGlobal)   //Local Scope
        {
            int offset = 0;          //The beginning position of the first static Frame in main memory that will be assigned to Process
            int mainMemPos = 0;      //Keeps track of the Frame position we are upto
            for(Process process : processes)
            {
                process.setMaxFrames(maxFrames);    //Assign the Process the max Frames allowed to be assigned
                process.setOffset(offset);          //Assign the current offset to the Process

                for(int i = 0; i < maxFrames; i++)
                {
                    frames.add(new Frame(process)); //Assign a Frame in the main memory to the Process
                    mainMemPos++;    //Increase position
                }
                
                offset = mainMemPos; //Update offset position
            }
        }
        else    //Global Scope
        {
            //Populate the Frame list 
            for(int i = 0; i < maxFrames; i++)
            {
                frames.add(new Frame());    
            }
            //Assign the Process the max Frames allowed to be assigned
            for(Process process : processes)
            {
                process.setMaxFrames(maxFrames);
            }
        }
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public boolean isPageInMemory(int pageID, Process process, boolean isGlobal)
    {
        System.out.println("MEMORY (IsPageInMemory)");
        int count = 0;
        for(Frame frame : frames)
        {
            System.out.println("Frame " + count + " : ID: " + frame.getPageID() + " OFFSET: " + " LRUTIME: " + frame.getLRUTime());
            count++;
        }
        System.out.println("PAGEID: " + pageID + " PROCESS: " + process.getID());
        if(!isGlobal)   //Local Scope
        {
            int mainMemPos = process.getOffset();
            for(int i = mainMemPos; i < mainMemPos + process.getMaxFrames(); i++)
            {
                if(frames.get(i).getPageID() == pageID)
                {
                    System.out.println("PAGE IS IN MEMORY");
                    return true;
                }
            }
        }
        else
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == pageID) //Only need to check if globally the page is loaded in main memory
                {
                    return true;    //Page is loaded in Memory
                }
            }
        }
        System.out.println("PAGE IS NOT IN MEMORY");
        return false; //Page Fault
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: Either demand paging has executed in terms of a local or global scope design OR a page replacement has been called
    public void addPage(int pageID, Process process, int time, boolean isGlobal)
    {
        boolean pageAdded = false;              //Stores whether or demand paging was successful or false if a page needs to be replaced
        
        if(!isGlobal)   //Local Scope
        {
            int mainMemPos = process.getOffset();   //Position in main memory where the first static Frame assigned to the Process is located
            System.out.println("DEMAND PAGING | MAIN_MEM_POS: " + mainMemPos + " PAGEADDED: " + pageAdded);
            System.out.println("MAX FRAMES: " + process.getMaxFrames());
            //Demand Paging for Static Allocation
            for(int i = mainMemPos; i < mainMemPos + process.getMaxFrames(); i++)
            {
                System.out.println("i: " + i + " PAGE_ID: " + frames.get(i).getPageID());
                if(frames.get(i).getPageID() == 0)
                {
                    System.out.println("Frame " + i + " : ID: " + frames.get(i).getPageID() + " PROCESS: " + frames.get(i).getProcess().getID() + " OFFSET: " + frames.get(i).getProcess().getOffset() +  " LRUTIME: " + frames.get(i).getLRUTime());
                    frames.get(i).setPageID(pageID);
                    frames.get(i).setLRUTime(time);
                    System.out.println("Frame " + i + " : ID: " + frames.get(i).getPageID() + " PROCESS: " + frames.get(i).getProcess().getID() + " OFFSET: " + frames.get(i).getProcess().getOffset() +  " LRUTIME: " + frames.get(i).getLRUTime());
                    pageAdded = true;
                    break;
                }
            }
        }
        else    //Global Scope
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == 0)
                {
                    frame.setPageID(pageID);
                    frame.setLRUTime(time);
                    pageAdded = true;
                    break;
                }
            }
        }

        //IF a page is required to be replaced
        if(!pageAdded)
        {
            System.out.println("PAGE NEEDS TO BE REPLACED");
            lruReplacePage(process, pageID, isGlobal);
        }
    }

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void lruReplacePage(Process process, int pageID, boolean isGlobal)
    {
        int lruTime; //Stores the least recently used Frame time
        int lruPagePos = 0;              //Stores the position of the least recently used Page in main memory

        //Find Least Recently Used Page in Main Memory 
        if(!isGlobal)   //Check frames allocated to the Process for a replacement (Local Scope)
        {
            int mainMemPos = process.getOffset();
            lruTime = frames.get(mainMemPos).getLRUTime();

            for(int i = mainMemPos; i < mainMemPos + process.getMaxFrames(); i++)
            {
                if(frames.get(i).getLRUTime() < lruTime && frames.get(i).getLRUTime() != 0)
                {
                    lruTime = frames.get(i).getLRUTime();
                    lruPagePos = i;
                }
            }

        }
        else    //Check all frames for potential replacement (Global scope)
        {
            lruTime = Integer.MAX_VALUE;

            for(Frame frame : frames)
            {
                if(frame.getLRUTime() < lruTime && frame.getLRUTime() != 0)
                {
                    lruTime = frame.getLRUTime();
                    lruPagePos = frames.indexOf(frame);
                }
            }
        }
        //Swap the LRU page out and the new page in.
        frames.get(lruPagePos).setPageID(pageID);
        frames.get(lruPagePos).setProcess(process);
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public void updateLRUTime(Process process, int pageID, int time, boolean isGlobal)
    {
        if(!isGlobal)   //Local Scope
        {
            int mainMemPos = process.getOffset();

            for(int i = mainMemPos; i < mainMemPos + process.getMaxFrames(); i++)
            {
                if(frames.get(i).getPageID() == pageID)
                {
                    frames.get(i).setLRUTime(time);
                    break;
                }
            }
        }
        else    //Global Scope
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == pageID)
                {
                    frame.setLRUTime(time);
                }
            }
        }
    }

    // ACCESSORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public ArrayList<Frame> getFrames()
    {
        return frames;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public int getTotalFrames()
    {
        return totalFrames;
    }
}

