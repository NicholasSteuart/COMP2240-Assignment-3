/*  
    * File: Process.java
    * Author: Nicholas Steuart c3330826
    * Date Created: 14/10/24
    * Date Last Modified: 23/10/24
    * Description: Implements the design and functionality of a Process to be run in a Round Robin CPU Scheduling Simulation with limited Memory
*/

// PACKAGES //

import java.util.ArrayList;

public class Process 
{
    // CLASS VARIABLES //

    private String name;                                        //The Process's Name
    private int id;                                             //The Process's ID
    private int turnTime = 0;                                   //Turnaround Time of the Process
    private ArrayList<Integer> pageRequests;                    //The Pages requested by the Process
    private int currentPage;                                    //What Page Request the Process is upto
    private int currentPagePos = 0;                             //The current Page position the Process is upto in it's Page requests (Initially starts at the first Page requested)
    private ArrayList<Integer> pageFaults = new ArrayList<>();  //List of Page Faults times that occur doing the Process's Lifetime. Also is used to return the number of faults that occurred
    private int offset;                                         //The position in the main memory where the frames assigned to the Process starts. Only used for Static Allocation
    private int blockedTime = 0;                                //The current amount of time the Process is required to be blocked for. 

    // CONSTRUCTORS //
    
    //PRE-CONDITION: No PRE-CONDITION
    //POST-CONDITION: Class variables name, id, pageRequests and currentPage instantiated with default values
    public Process()
    {
        name = "";
        id = 0;
        pageRequests = new ArrayList<>();
        currentPage = 0;
    }
    //PRE-CONDITION: 
        //Parameter id must be greater than zero
        //Parameter pageRequests cannot be null
    //POST-CONDITION: Specialised Constructor instantiated with Parameters name, id and pageRequests having mutated their respective Class variables
    public Process(String name, int id, ArrayList<Integer> pageRequests)
    {
        this.name = name;
        this.id = id;
        this.pageRequests = pageRequests;
        currentPage = pageRequests.get(0);
    }

    // METHODS //

    //PRE-CONDITION: Parameter time must be a non-negative Integer
    //POST-CONDITION: Class variable pageFaults contains a new Integer element of value time
    public void addPageFault(int time)
    {
        pageFaults.add(time);
    }
    //PRE-CONDITION: pageRequests cannot be null
    //POST-CONDITION: The currentPage has stored the next Page required by the Process
    public void nextPage()
    {
        currentPagePos++;
        currentPage = pageRequests.get(currentPagePos); //currentPage points to the next page
    }
    //PRE-CONDITION: pageRequests cannot be null
    //POST-CONDITION: Boolean value returned True IF currentPagePos has reached the last element position in pageRequests
    public boolean isFinished()
    {  
        if(currentPagePos == pageRequests.size() - 1)   
        {
            return true;    //Process has finished executing all of it's pageRequests
        }
        return false;   //Not finished
    }

    // MUTATORS //

    //PRE-CONDITION: Process Constructor instantiated and Parameter turnTime is a non-negative Integer
    //POST-CONDITION: Class variable turnTime mutated by parameter turnTime
    public void setTurnTime(int turnTime)
    {
        this.turnTime = turnTime;
    }
    //PRE-CONDITION: Process Constructor instantiated and Parameter currentPage is a non-negative Integer
    //POST-CONDITION: Class variable currentPage mutated by parameter currentPage
    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }
    //PRE-CONDITION: Process Constructor instantiated and Parameter offset is a non-negative Integer
    //POST-CONDITION: Class variable offset mutated by parameter offset
    public void setOffset(int offset)
    {
        this.offset = offset;
    }
    //PRE-CONDITION: Process Constructor instantiated and Parameter blockedTime is of the value 4
    //POST-CONDITION: Class variable blockedTime mutated by parameter blockedTime
    public void setBlockedTime(int blockedTime)
    {
        this.blockedTime = blockedTime;
    }

    // ACCESSORS //

    //PRE-CONDITION: Process Constructor instantiated
    //POST-CONDITION: Class variable name returned 
    public String getName()
    {
        return name;
    }
    //PRE-CONDITION: Process Constructor instantiated and id must be greater than 0
    //POST-CONDITION: Class variable id returned
    public int getID()
    {
        return id;
    }
    //PRE-CONDITION: Process Constructor instantiated and turnTime must be a non-negative Integer
    //POST-CONDITION: Class variable turnTime returned
    public int getTurnTime()
    {
        return turnTime;
    }
    //PRE-CONDITION: Process Constructor instantiated and pID must be a non-negative Integer
    //POST-CONDITION: Class variable currentPage returned
    public int getCurrentPage()
    {
        return currentPage;
    }
    //PRE-CONDITION: Process Constructor instantiated and pageRequests must not be null
    //POST-CONDITION: Class variable pageRequests returned
    public ArrayList<Integer> getPageRequests()
    {
        return pageRequests;
    }
    //PRE-CONDITION: Process Constructor instantiated and pageFaults must not be null
    //POST-CONDITION: Class variable pageFaults returns it's size
    public int getTotalPageFaults()
    {
        return pageFaults.size();
    }
    //PRE-CONDITION: Process Constructor instantiated and pageFaults must not be null
    //POST-CONDITION: String faultTimes storing pageFaults in the Assignment-Specified format {element_1, element_2, ..., element_n} returned 
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
    //PRE-CONDITION: Process Constructor instantiated and offset must be a non-negative Integer
    //POST-CONDITION: Class variable offset returned
    public int getOffset()
    {
        return offset;
    }
    //PRE-CONDITION: Process Constructor instantiated and blockedTime must be of the form: 0 <= blockedTime <= 4
    //POST-CONDITION: Class variable blockedTime returned
    public int getBlockedTime()
    {
        return blockedTime;
    }
}
