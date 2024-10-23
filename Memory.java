/* File: Memory.java
* Author: Nicholas Steuart c3330826
* Date Created: 14/10/24
* Date Last Modified: 23/10/24
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
    private int maxFrames;              //The maximum amount of Frames a Process can have. Used exclusively for Static Allocation

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
        //Assign Frames to Processes based on Resident Set Size Management
        if(!isGlobal)   //Static Allocation
        {
            maxFrames = totalFrames / processes.size();
            int offset = 0;          //The beginning position of the first static Frame in main memory that will be assigned to Process
            int mainMemPos = 0;      //Keeps track of the Frame position we are upto
            for(Process process : processes)
            {
                process.setOffset(offset);          //Assign the current offset to the Process

                for(int i = 0; i < maxFrames; i++)
                {
                    frames.add(new Frame(process)); //Assign a Frame in the main memory to the Process
                    mainMemPos++;    //Increase position
                }
                
                offset = mainMemPos; //Update offset position
            }
        }
        else    //Variable Allocation
        {
            //Populate the Frame list 
            for(int i = 0; i < totalFrames; i++)
            {
                frames.add(new Frame());    
            }
        }
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public boolean isPageInMemory(int pageID, Process process, boolean isGlobal)
    {
        if(!isGlobal)   //Static Allocation
        {
            int mainMemPos = process.getOffset();
            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
            {
                if(frames.get(i).getPageID() == pageID)
                {
                    return true;
                }
            }
        }
        else    //Variable Allocation: Since a Process can have a dynamic amount of frames, check all frames for the Frames allocated to the Process AND if amongst those allocated frames the Page required is loaded in
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == pageID && frame.getProcess().getID() == process.getID()) 
                {
                    return true;    //Page is loaded in Memory
                }
            }
        }

        return false; //Page Fault
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: Either demand paging has executed in terms of a local or global scope design OR a page replacement has been called
    public void addPage(int pageID, Process process, int time, boolean isGlobal)
    {
        boolean pageAdded = false;              //Stores whether or demand paging was successful or false if a page needs to be replaced
        
        if(!isGlobal)   //Static Allocation
        {
            int mainMemPos = process.getOffset();   //Position in main memory where the first static Frame assigned to the Process is located
            //Demand Paging for Static Allocation
            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
            {
                if(frames.get(i).getPageID() == 0)
                {
                    frames.get(i).setPageID(pageID);
                    frames.get(i).setLRUTime(time);
                    pageAdded = true;
                    break;
                }
            }
        }
        else    //Variable Allocation
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == 0)
                {
                    frame.setPageID(pageID);
                    frame.setProcess(process);
                    frame.setLRUTime(time);
                    pageAdded = true;
                    break;
                }
            }
        }

        //IF a page is required to be replaced
        if(!pageAdded)
        {
            lruReplacePage(process, pageID, time, isGlobal);
        }
    }

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void lruReplacePage(Process process, int pageID, int time, boolean isGlobal)
    {
        int lruTime = Integer.MAX_VALUE;                     //Stores the least recently used Frame time
        int lruPagePos = 0;              //Stores the position of the least recently used Page in main memory

        //Find Least Recently Used Page in Main Memory 
        if(!isGlobal)   //Check frames allocated to the Process for a replacement (Local Scope)
        {
            int mainMemPos = process.getOffset();

            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
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
        frames.get(lruPagePos).setLRUTime(time);
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public void updateLRUTime(Process process, int pageID, int time, boolean isGlobal)
    {
        if(!isGlobal)   //Local Scope
        {
            int mainMemPos = process.getOffset();

            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
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
                if(frame.getProcess() == null)
                {
                    continue;
                }
                if(frame.getPageID() == pageID && frame.getProcess().getID() == process.getID())
                {
                    frame.setLRUTime(time);
                }
            }
        }
    }

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void reallocateMemory(Process process)
    {
        for(Frame frame : frames)
        {
            if(frame.getProcess() == null)
            {
                continue;
            }
            if(frame.getProcess().getID() == process.getID())
            {
                frame.setPageID(0);
                frame.setProcess(null);
                frame.setLRUTime(0);
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

