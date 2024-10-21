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
    public boolean isPageInMemory(int pageID, Process process)
    {
        for(Frame frame : frames)
        {
            if(frame.getPageID() == pageID && frame.getProcess() == process)
            {
                return true;    //Page is loaded in Memory
            }
        }

        return false; //Page Fault
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void addPage(int pageID, Process process, boolean isGlobal)
    {
        boolean pageAdded = false;              //Stores whether or demand paging was successful or false if a page needs to be replaced
        
        if(!isGlobal)   //Local Scope
        {
            int mainMemPos = process.getOffset();   //Position in main memory where the first static Frame assigned to the Process is located

            //Demand Paging for Static Allocation
            for(int i = mainMemPos; i < process.getMaxFrames(); i++)
            {
                if(frames.get(i).getPageID() == 0)
                {
                    frames.get(i).setPageID(pageID);
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
                    pageAdded = true;
                    break;
                }
            }
        }

        //IF a page is required to be replaced
        if(!pageAdded)
        {
            lruReplacePage(process, pageID, isGlobal);
        }
    }

    // TODO: REWRITE THIS //
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void lruReplacePage(Process process, int pageID, boolean isGlobal)
    {
        Frame lruFrame = null;

        //Find Least Recently Used Page in Main Memory 
        if(!isGlobal)   //Check frames allocated to the Process for a replacement (Local Scope)
        {
            for(Frame frame : frames)
            {
                if(frame.getProcess() == process && lruFrame == null)
                {
                    lruFrame = frame;
                }
                else if(frame.getProcess() == process && lruFrame != null)
                {
                    if(frame.getLRUTime() > lruFrame.getLRUTime())
                    {
                        lruFrame = frame;
                    }
                }
            }
        }
        else    //Check all frames for potential replacement (Global scope)
        {
            lruFrame = frames.get(0);
            for(Frame frame : frames)
            {
                if(frame.getLRUTime() > lruFrame.getLRUTime())
                {
                    lruFrame = frame;
                }
            }
        }
        //Swap the LRU page out and the new page in
        lruFrame.setPageID(pageID);
        lruFrame.setProcess(process);

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

