/* File: Process.java
* Author: Nicholas Steuart c3330826
* Date Created: 14/10/24
* Date Last Modified: 14/10/24
* Description: The Process class represents a process in a CPU scheduling simulation. 
* Each process contains essential information that is used by various 
* CPU scheduling algorithms to manage and prioritize execution order.
*/

// PACKAGES //

import java.util.ArrayList;

public class Process 
{
    // CLASS VARIABLES //

    private String name;                                        //The Process's Name
    private int id;                                             //The Process's ID
    private int turnTime = 0;                                   //Turnaround Time of the Process
    private ArrayList<Integer> pageRequests;                    //The pages requested by the Process
    private int currentPage;                                    //What Page Request the Process is upto
    private int currentPagePos = 0;                             //The current position the Process is upto in the pageRequests
    private ArrayList<Integer> pageFaults = new ArrayList<>();  //List of Page Faults times that occur doing the Process's Lifetime
    private int offset;                                         //The position in the main memory where the frames assigned to the Process starts. Only used for Static Allocation
    private int maxFrames;                                      //Total Frames allowed to be allocated to a Process. Only used for Static Allocation
    private int blockedTime = 0;                                //The time spent by the Process blocked

    // CONSTRUCTORS //
    
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Process()
    {
        name = "";
        id = 0;
        pageRequests = new ArrayList<>();
        currentPage = 0;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Process(String name, int id, ArrayList<Integer> pageRequests)
    {
        this.name = name;
        this.id = id;
        this.pageRequests = pageRequests;
        currentPage = pageRequests.get(0);
    }

    // METHODS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void addPageFault(int time)
    {
        pageFaults.add(time);
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void nextPage()
    {
        currentPagePos++;
        currentPage = pageRequests.get(currentPagePos);
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public boolean isFinished()
    {  
        if(currentPagePos == pageRequests.size() - 1)
        {
            return true;
        }
        return false;
    }

    // MUTATORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setTurnTime(int turnTime)
    {
        this.turnTime = turnTime;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setMaxFrames(int maxFrames)
    {
        this.maxFrames = maxFrames;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setBlockedTime(int blockedTime)
    {
        this.blockedTime = blockedTime;
    }

    // ACCESSORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public String getName()
    {
        return name;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getID()
    {
        return id;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getTurnTime()
    {
        return turnTime;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getCurrentPage()
    {
        return currentPage;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getPos()
    {
        return currentPagePos;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public ArrayList<Integer> getPageRequests()
    {
        return pageRequests;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getTotalPageFaults()
    {
        return pageFaults.size();
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public String getFaultTimes()
    {
        String faultTimes = "{";
        for(int i = 0; i < pageFaults.size() - 1; i++)
        {
            faultTimes += pageFaults.get(i) + ", ";
        }
        faultTimes += pageFaults.get(pageFaults.size() - 1) + "}";
        return faultTimes;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getOffset()
    {
        return offset;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getMaxFrames()
    {
        return maxFrames;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getBlockedTime()
    {
        return blockedTime;
    }
}
