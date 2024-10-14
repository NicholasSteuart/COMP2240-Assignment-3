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

    private String name;                            //The Process's Name
    private int id;                                 //The Process's ID
    private int turnTime = 0;                       //Turnaround Time of the Process
    private ArrayList<Integer> pageRequests;        //The pages requested by the Process
    private int currentPage;
    private ArrayList<Integer> pageFaults = new ArrayList<>();  //List of Page Faults times that occur doing the Process's Lifetime
    private int framesAllocated = 0;                //Frames allocated to the Process by the Memory

    // CONSTRUCTORS //
    
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Process()
    {
        name = "";
        id = 0;
        pageRequests = null;
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
    public int nextPage()
    {
        if(currentPage < pageRequests.size())
        {
            return pageRequests.get(currentPage++);
        }
        return -1;  //No more page requests left
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public boolean isFinished()
    {
        return currentPage >= pageRequests.size();
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
    public void setFramesAllocated(int framesAllocated)
    {
        this.framesAllocated = framesAllocated;
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
    public ArrayList<Integer> getPageRequests()
    {
        return pageRequests;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public ArrayList<Integer> getPageFaults()
    {
        return pageFaults;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getFramesAllocated()
    {
        return framesAllocated;
    }
}
