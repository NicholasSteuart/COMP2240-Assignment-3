/* 
    * COMP2240 Assignment 3
    * File: Memory.java
    * Author: Nicholas Steuart c3330826
    * Date Created: 14/10/24
    * Date Last Modified: 23/10/24
    * Description: Simulates main memory in a computing environment. 
    * Contains implementation of the 2 Least Recently Used (LRU) Replacement algorithms required by the assignment: 
    * 1. Static Allocation with Local Replacement Scope, and
    * 2. Variable Allocation with Global Replacement Scope
*/

// PACKAGES //

import java.util.ArrayList;

public class Memory 
{
    // CLASS VARIABLES //

    private ArrayList<Frame> frames;    //The main memory
    private int totalFrames;            //The total amount of Frames in main memory
    private int maxFrames;              //The maximum amount of Frames a Process can have. Used exclusively for Static Allocation
    private int mainMemPos;             //Keeps track of the index we are upto in main memory. Used exclusively for Static Allocation

    // CONSTRUCTORS //

    //PRE-CONDITION: No PRE-CONDITION
    //POST-CONDITION: Class variables frames and totalFrames instantiated with default values
    public Memory()
    {
        frames = null;
        totalFrames = 0;
    }
    //PRE-CONDITION: Parameter totalFrames must be greater than 0
    //POST-CONDITION: Specialised Constructor instantiated with 
        //Parameter totalFrames having mutated Class variable totalFrames. 
        //Class variable frames instantiated with an initial capacity of totalFrames
    public Memory(int totalFrames)
    {
        this.totalFrames = totalFrames;
        frames = new ArrayList<>(totalFrames);
    }

    // METHODS //
    
    //PRE-CONDITION: 
        //Parameter processes cannot be null
        //Parameter isGlobal is either:
            //True IF Variable Allocation with Global Replacement Scope
            //False IF Static Allocation with Local Replacement Scope
    //POST-CONDITION: Class variable frames set-up in accordance with the resident set management policy designated by the isGlobal parameter
    public void allocateMemory(ArrayList<Process> processes, boolean isGlobal)
    {   
        //Assign Frames to Processes based on Resident Set Size Management
        if(!isGlobal)   //Static Allocation: Allocate Fixed, equal-sized sets of Frames from the main memory to each Process 
        {
            maxFrames = totalFrames / processes.size(); //Determines how many Frames a Process can be statically allocated
            int offset = 0;     //The beginning index of the first static Frame in main memory that is assigned to a Process
            mainMemPos = 0;     //Set to the first Frame in main memory

            for(Process process : processes)
            {
                process.setOffset(offset);          //Assign the current offset to the Process

                for(int i = 0; i < maxFrames; i++)
                {
                    frames.add(new Frame(process)); //Assign a Frame in the main memory to the Process
                    mainMemPos++;                   //Increase index
                }
                
                offset = mainMemPos; //Update offset position to reflect where the next Process should be allocated it's resident set of static Frames
            }
        }
        else    //Variable Allocation: All Frames in main memory are free to be allocated to a Process so they are just instantiated as available
        {
            //Populate the Frame ArrayList frames with available Frames 
            for(int i = 0; i < totalFrames; i++)
            {
                frames.add(new Frame());    
            }
        }
    }
    //PRE-CONDITION:
        //Parameter pageID must be greater than zero
        //Parameter process cannot be null
        //Parameter isGlobal is either:
            //True IF Variable Allocation with Global Replacement Scope
            //False IF Static Allocation with Local Replacement Scope
    //POST-CONDITION: Returns True if the Page is loaded in main memory and False otherwise, respective of the Resident Set Scheme dictated by isGlobal
    public boolean isPageInMemory(int pageID, Process process, boolean isGlobal)
    {
        if(!isGlobal)   //Static Allocation: Is the Page required loaded into the fixed set of Frames allocated to the Process?
        {
            mainMemPos = process.getOffset();   //Position in main memory where the first static Frame assigned to the Process is located

            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
            {
                if(frames.get(i).getPageID() == pageID)
                {
                    return true;    //Page is loaded in main memory
                }
            }
        }
        else    //Variable Allocation:r Is the Page required and owned by the Process loaded into any Frame in the main memory?
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == pageID && frame.getProcess().getID() == process.getID()) 
                {
                    return true;    //Page is loaded in main memory
                }
            }
        }

        return false; //Page is not loaded in main memory (Page Fault)
    }
    //PRE-CONDITION: 
        //Parameter pageID must be greater than zero
        //Parameter process cannot be null
        //Parameter time must be a non-negative Integer
        //Parameter isGlobal is either:
            //True IF Variable Allocation with Global Replacement Scope
            //False IF Static Allocation with Local Replacement Scope
    //POST-CONDITION: 
        //Either a Page containing pageID, process and time data has been loaded into a Frame in main memory following the resident set management scheme dictated by isGlobal, OR
        //The main memory is full and a Page Replacement is called following along with the Replacement Scope dictated by isGlobal
    public void addPage(int pageID, Process process, int time, boolean isGlobal)
    {
        boolean pageAdded = false;  //Flag that checks if the page was successfully added to main memory
        
        if(!isGlobal)   //Static Allocation: Find the first free fixed-Frame that the Process has been allocated and allocate the Page to it
        {
            mainMemPos = process.getOffset();   //Position in main memory where the first static Frame assigned to the Process is located

            //Demand Paging for Static Allocation
            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
            {
                //IF we find a static Frame allocated to the Process that is not being used, The Page is allocated to the Frame
                if(frames.get(i).getPageID() == 0)
                {
                    //Load the Page data into the Frame
                    frames.get(i).setPageID(pageID);
                    frames.get(i).setLRUTime(time);
                    pageAdded = true;   //We have successfully allocated a Page so we do not need to replace a Page
                    break;              //Break as to not allocate the Page multiple times
                }
            }
        }
        else    //Variable Allocation: Find the first non-allocated Frame in main memory to load the Page into
        {
            for(Frame frame : frames)
            {
                if(frame.getPageID() == 0) //Same as line 139 except we can allocate to any Frame in main memory
                {
                    //Load the Page data into the Frame
                    frame.setPageID(pageID);
                    frame.setProcess(process);
                    frame.setLRUTime(time);
                    pageAdded = true;   //Same as line 144
                    break;              //Same as line 145
                }
            }
        }

        //IF the Page could not be loaded into main memory, call for a Page Replacement 
        if(!pageAdded)
        {
            lruReplacePage(pageID, process, time, isGlobal);
        }
    }
    //PRE-CONDITION: 
        //Parameter pageID must be greater than zero
        //Parameter process cannot be null
        //Parameter time must be a non-negative Integer
        //Parameter isGlobal is either:
            //True IF Variable Allocation with Global Replacement Scope
            //False IF Static Allocation with Local Replacement Scope
    //POST-CONDITION: 
        //The Least Recently Used Page is unloaded from main memory and the new Page is loaded in based on the Replacement Policy dictated by isGlobal
    public void lruReplacePage(int pageID, Process process, int time, boolean isGlobal)
    {
        int lruTime = Integer.MAX_VALUE; //Stores the least recently used Frame time. Instantiated as MAX_VALUE to always parse the first frame allocated to the Process to modify this value (Assuming the system does not run until MAX_VALUE, which is unrealistic)
        int lruPagePos = 0;              //Stores the position of the least recently used Page in main memory which will be replaced

        //Find the Least Recently Used Page in main memory 
        if(!isGlobal)   //Local Replacement Scope: Check the fixed-frames allocated to the Process for a replacement 
        {
            mainMemPos = process.getOffset();   //Position in main memory where the first static Frame assigned to the Process is located

            //Find the Least Recently Used Page position in the main memory
            for(int i = mainMemPos; i < mainMemPos + maxFrames; i++)
            {
                if(frames.get(i).getLRUTime() < lruTime && frames.get(i).getLRUTime() != -1) 
                {
                    lruTime = frames.get(i).getLRUTime();   //Current LRU Page
                    lruPagePos = i;                         //Current Page to replace
                }
            }
        }
        else    //Global Replacement Scope: Check all Frames in main memory for the least recently used Frame
        {
            //Find the Least Recently Used Page position in the main memory
            for(Frame frame : frames)
            {
                if(frame.getLRUTime() < lruTime && frame.getLRUTime() != -1)
                {
                    lruTime = frame.getLRUTime();       //Current LRU Page
                    lruPagePos = frames.indexOf(frame); //Current Page to replace
                }
            }
        }
        //Replace the old LRU Page with the new Page
        frames.get(lruPagePos).setPageID(pageID);
        frames.get(lruPagePos).setProcess(process);
        frames.get(lruPagePos).setLRUTime(time);
    }
    //PRE-CONDITION: 
        //Parameter pageID must be greater than zero
        //Parameter process cannot be null
        //Parameter time must be a non-negative Integer
    //POST-CONDITION: The Page parsed in has it's lru time updated to time (Scope is not required as we can just find the Frame that has the Page loaded)
    public void updateLRUTime(int pageID, Process process, int time)
    {
        for(Frame frame : frames)
        {
            //IF the Frame has not been allocated yet
            if(frame.getProcess() == null) continue;

            //IF the frame holds the Page we are after
            if(frame.getPageID() == pageID && frame.getProcess().getID() == process.getID()) frame.setLRUTime(time); //Update the lru time to time
        }
    }
    //PRE-CONDITION: Parameter process cannot be null
    //POST-CONDITION: All Frames allocated to the Process are deallocated and reset to default values
    public void reallocateMemory(Process process)
    {
        //Reset all Frames that are allocated to the finished Process
        for(Frame frame : frames)
        {
            //IF the Frame has not been allocated yet
            if(frame.getProcess() == null) continue;

            //IF the Frame is allocated to the Finished Process
            if(frame.getProcess().getID() == process.getID())
            {
                //Reset Frame to default values
                frame.setPageID(0);
                frame.setProcess(null);
                frame.setLRUTime(-1);
            }
        }
    }

    // ACCESSORS //

    //PRE-CONDITION: Memory Constructor instantiated and frames cannot be null
    //POST-CONDITION: Class variable frames returned
    public ArrayList<Frame> getFrames()
    {
        return frames;
    }
    //PRE-CONDITION: Memory Constructor instantiated and totalFrames must be greater than zero
    //POST-CONDITION: Class variable totalFrames returned
    public int getTotalFrames()
    {
        return totalFrames;
    }
}

