/* File: Scheduler.java
* Author: Nicholas Steuart c3330826
* Date Created: 15/10/24
* Date Last Modified: 15/10/24
* Description: Implements the Round Robin Short-term Scheduling Algorithm
*/

// PACKAGES //

import java.util.ArrayList;
import java.util.Comparator;

public class Scheduler
{
    // CLASS VARIABLES //

    private ArrayList<Process> readyQueue;      //Ready Queue of Processes
    private ArrayList<Process> blockedQueue = new ArrayList<>();    //Blocked Queue of Processes
    private ArrayList<Process> finishedQueue = new ArrayList<>();   //Finished Queue of Processes
    private int timer = 0;                       //The global time of the simulation as it runs
    private int timeSlice;                      //Quantum of time that a Process is allowed to run for before having to swap
    private Memory memory;                      //Memory the Scheduler has access to
    private boolean isGlobal;                   //Determines what Page Replacement Policy is used by the Scheduler.
                                                //True = Variable Allocation with Global Scope
                                                //False = Static Allocation with Local Scope
    // CONSTRUCTORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Scheduler()
    {
        readyQueue = new ArrayList<>();
        blockedQueue = new ArrayList<>();
        timeSlice = 0;
        memory = new Memory();
        isGlobal = false;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Scheduler(ArrayList<Process> readyQueue, int timeSlice, Memory memory, boolean isGlobal)
    {
        this.readyQueue = readyQueue;
        this.timeSlice = timeSlice;
        this.memory = memory;
        this.isGlobal = isGlobal;
    }

    // METHODS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public void run()
    {
        memory.allocateMemory(readyQueue, isGlobal);
        for(Process process : readyQueue)
        {
            System.out.println("PROCESS: " + process.getName() + " PROCESS_ID: " + process.getID() + " PAGE REQUESTS: " + process.getPageRequests());
        }
        int totalProcesses = readyQueue.size();

        while(finishedQueue.size() < totalProcesses)
        {
            System.out.println("READY QUEUE:");
            for(Process process : readyQueue)
            {
                System.out.println("PROCESS: " + process.getID());
            }
            //IF no Processes are able to run
            if(readyQueue.isEmpty())
            {
                System.out.println("READY_QUEUE_EMPTY");
                checkBlocked(); //Check to see if a Process has become unblocked
                timer++;    //Increase the Timer

            }
            else
            {
                Process runningProcess = readyQueue.get(0); //Get the next ready Process ready to run

                for(int i = 0; i < timeSlice; i++)
                {
                    int pageNeeded = runningProcess.getCurrentPage(); //The Page that the Process requires now
                    System.out.println("TIME: " + timer + " RUNNING_PROCESS: " + runningProcess.getID() + " CURRENT_PAGE: " + runningProcess.getCurrentPage() + " PAGE_NEEDED: " + pageNeeded + " OFFSET: " + runningProcess.getOffset());
                    if(!memory.isPageInMemory(pageNeeded, runningProcess, isGlobal))  //IF the Page required by the Process is not loaded in main memory, the interrupt routine runs 
                    {
                        System.out.println("PAGE FAULT");
                        handlePageFault(pageNeeded, runningProcess);    //Interrupt Routine
                        break;                                          //Running Process is blocked so it's alloited time slice expires
                    }
                    else    //Process executes 1 unit of it's time slice
                    {
                        memory.updateLRUTime(runningProcess, pageNeeded, timer, isGlobal);
                        timer++;                                                            //Increase time
                        checkBlocked();                                                     //Check to see if a Process has become unblocked
                        //Check to see if the running Process has finished 
                        System.out.println("CURRENT_PAGE_POSITION: " + runningProcess.getPos() + " NEXT PAGE REQUIRED: " + runningProcess.getCurrentPage());
                        if(runningProcess.isFinished())
                        {
                            System.out.println("PROCESS " + runningProcess.getID() + " HAS FINISHED");
                            runningProcess.setTurnTime(timer);  //Set Turnaround Time of the Process
                            //Move the Process to the finished queue
                            finishedQueue.add(runningProcess);  
                            readyQueue.remove(runningProcess);
                            break;
                        }
                        if(i == timeSlice - 1)
                        {
                            readyQueue.add(runningProcess);
                            readyQueue.remove(0);
                        }
                        runningProcess.nextPage();                                          //Set the Process's next Page required to execute 
                    }
                }
                System.out.println("FINISHED_QUEUE");
                for(Process process : finishedQueue)
                {
                    System.out.println("PROCESS: " + process.getID());
                }
            }
        }
    }

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public void checkBlocked()
    {
        for(Process process : blockedQueue)
        {
            System.out.println("BLOCKED: " + process.getID() + " BLOCK_TIME: " + process.getBlockedTime());
            process.setBlockedTime(process.getBlockedTime() - 1);   //Increment the time spent swapping
        }
        //Move a ready Process back to the ready queue and remove from the blocked queue
        blockedQueue.removeIf(element ->
        {
            if(element.getBlockedTime() == 0)
            {
                readyQueue.add(element);
                return true;
            }
            return false;
        });
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public void handlePageFault(int pageNeeded, Process process)
    {
        process.setBlockedTime(4);  //Process blocks for 4 time units
        process.addPageFault(timer);            //Add a Page Fault Time to the Process
        System.out.println("PROCESS: " + process.getID() + " FAULT_TIMES: " + process.getFaultTimes());
        blockedQueue.add(process);              //Move the Process to the Blocked queue
        readyQueue.remove(process);
        memory.addPage(pageNeeded, process, timer, isGlobal);   //Allocate the Page to main memory
    }

    // ACCESSORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public boolean isGlobal()
    {
        return isGlobal;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public ArrayList<Process> getFinishedQueue()
    {
        finishedQueue.sort(Comparator.comparingInt(p -> p.getID()));
        return finishedQueue;
    }
}
