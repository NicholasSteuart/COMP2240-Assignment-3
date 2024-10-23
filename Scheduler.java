/* 
    * COMP2240 Assignment 3
    * File: Scheduler.java
    * Author: Nicholas Steuart c3330826
    * Date Created: 15/10/24
    * Date Last Modified: 23/10/24
    * Description: Implements the Round Robin Short-term Scheduling Algorithm. 
    * Due to the limited memory, the scheduler handles page faults using an interrupt routine
*/

// PACKAGES //

import java.util.ArrayList;
import java.util.Comparator;

public class Scheduler
{
    // CLASS VARIABLES //

    private ArrayList<Process> readyQueue;                          //Ready Queue of Processes
    private ArrayList<Process> blockedQueue = new ArrayList<>();    //Blocked Queue of Processes
    private ArrayList<Process> finishedQueue = new ArrayList<>();   //Finished Queue of Processes
    private int timer = 0;                                          //The global time of the simulation as it runs
    private int timeSlice;                                          //Quantum of time or time slice that a Process is allowed to run for before having to be placed at the back of the readyQueue
    private Memory memory;                                          //The main memory the Scheduler has access to
    private boolean isGlobal;                                       //Determines what Page Replacement Policy is used by the Scheduler:
                                                                        //True = Variable Allocation with Global Scope
                                                                        //False = Static Allocation with Local Scope

    // CONSTRUCTORS //

    //PRE-CONDITION: No PRE-CONDITION
    //POST-CONDITION: Class variables readyQueue, blockedQueue, timeSlice, memory, and isGlobal instantiated with default values (isGlobal being a boolean is defaulted to false; Static Allocation with Local Scope is not considered a default)
    public Scheduler()
    {
        readyQueue = new ArrayList<>();
        blockedQueue = new ArrayList<>();
        timeSlice = 0;
        memory = new Memory();
        isGlobal = false;
    }
    //PRE-CONDITION: 
        //Parameter readyQueue cannot be null
        //Parameter timeSlice must be an Integer (The assumption can be made however that the timeSlice will be greater than zero)
        //Parameter memory cannot be null
        //Parameter isGlobal is either:
            //True IF Variable Allocation with Global Replacement Scope
            //False IF Static Allocation with Local Replacement Scope
    //POST-CONDITION: Specialised Constructor instantiated with Parameters readyQueue, timeSlice, memory, and isGlobal having mutated Class variable readyQueue, timeSlice, memory, and isGlobal
    public Scheduler(ArrayList<Process> readyQueue, int timeSlice, Memory memory, boolean isGlobal)
    {
        this.readyQueue = readyQueue;
        this.timeSlice = timeSlice;
        this.memory = memory;
        this.isGlobal = isGlobal;
    }

    // METHODS //

    //PRE-CONDITION: Scheduler Constructor must be instantiated
    //POST-CONDITION: The Scheduler has ran the Round Robin Scheduling Simulation based on the Resident Set Management and Replacement Scope dictated by the Class variable isGlobal
    public void run()
    {
        memory.allocateMemory(readyQueue, isGlobal);    //Allocate Memory
        int totalProcesses = readyQueue.size();         //Used as a check to determine if the finishedQueue has finished running every Process in the simulation

        while(finishedQueue.size() < totalProcesses)    //Runs until every Process in the simulation has finished
        {
            //IF no Processes are in the ready queue
            if(readyQueue.isEmpty())
            {
                checkBlocked(); //Check to see if a Process has become unblocked
                timer++;        //Increase the timer while the system idles
            }
            else
            {
                Process runningProcess = readyQueue.get(0); //The next ready process starts running

                for(int i = 0; i < timeSlice; i++)  //Give the Process it's time slice
                {
                    int pageNeeded = runningProcess.getCurrentPage(); //The Page that the Process requires now
                    
                    if(!memory.isPageInMemory(pageNeeded, runningProcess, isGlobal))  //IF the Page required by the Process is not loaded in main memory, the interrupt routine runs 
                    {
                        handlePageFault(pageNeeded, runningProcess);    //Interrupt Routine
                        break;                                          //Running Process is blocked so it's alloited time slice expires
                    }
                    else    //Process executes 1 unit of it's time slice
                    {
                        memory.updateLRUTime(pageNeeded, runningProcess, timer);            //Update the lru time of the Page to the time of the system as the Page has just been used 
                        timer++;                                                            //Increase the timer as it takes 1 time unit to execute a single instruction (A Page)
                        checkBlocked();                                                     //Check to see if a Process has become unblocked now the time has increased in accordance with part f of 2.4 Scheduling in the assignment specification
                        
                        //Check to see if the running Process has finished 
                        if(runningProcess.isFinished())
                        {
                            runningProcess.setTurnTime(timer);          //Set Turnaround Time of the Process
                            memory.reallocateMemory(runningProcess);    //Reallocate Memory of any Frames allocated to the finished Process (Only affects Variable Allocation, Static Allocation will not be affected)
                            
                            //Move the Process to the finished queue
                            finishedQueue.add(runningProcess);  
                            readyQueue.remove(runningProcess);
                            break;                                      //Break the loop as the Process is finished
                        }
                        //If the Process still has instructions to execute but it's time slice has expired
                        if(i == timeSlice - 1)
                        {
                            //Add the running Process to the back of the Ready Queue
                            readyQueue.add(runningProcess);
                            readyQueue.remove(0);
                        }

                        runningProcess.nextPage();                                          //Set the Process's next Page required to execute 
                    }
                }
            }
        }
    }

    //PRE-CONDITION: Scheduler Constructor must be instantiated
    //POST-CONDITION: IF a Process has finished blocking, the Process has finished it's I/O Request and the Page it required has been loaded into main memory
    public void checkBlocked()
    {
        //Decrement the blocked time remaining on all Processes in the blocked queue
        for(Process process : blockedQueue)
        {
            process.setBlockedTime(process.getBlockedTime() - 1);   //Decrement the time spent swapping
        }
        //If a Process has finished blocking...
        blockedQueue.removeIf(element ->
        {
            if(element.getBlockedTime() == 0)                                         //The Process has finished blocking
            {
                readyQueue.add(element);                                              //Move the Process back into the ready Queue
                memory.addPage(element.getCurrentPage(), element, timer, isGlobal);   //Load the Page into main memory
                return true;
            }

            return false;                                                             //The Process is still blocked
        });
    }
    //PRE-CONDITION: 
        //Parameter pageNeeded must be greater than zero
        //Parameter process cannot be null
    //POST-CONDITION: Process is interrupted and blocked
    public void handlePageFault(int pageNeeded, Process process)
    {
        process.setBlockedTime(4);  //Process blocks for 4 time units
        process.addPageFault(timer);            //Add a Page Fault Time to the Process
        blockedQueue.add(process);              //Move the Process to the Blocked queue
        readyQueue.remove(process);             //Remove from te ready queue
    }
    // ACCESSORS //

    //PRE-CONDITION: Scheduler Constructor instantiated
    //POST-CONDITION: Class variable isGlobal returned
    public boolean isGlobal()
    {
        return isGlobal;
    }
    //PRE-CONDITION: Scheduler Constructor instantiated and finishedQueue cannot be null
    //POST-CONDITION: Class variable finishedQueue (sorted in increasing order of the Process IDs in the list) returned 
    public ArrayList<Process> getFinishedQueue()
    {
        finishedQueue.sort(Comparator.comparingInt(p -> p.getID()));    //Sorts the finishedQueue
        return finishedQueue;
    }
}
