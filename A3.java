/* COMP2240 Assignment 3
 * File: A3.java
 * Author: Nicholas Steuart c3330826
 * Date Created: 27/9/24
 * Date Last Modified: 23/10/24
 * Description: MAIN file. Input and Output for Assignment 3
 */

// PACKAGES //

import java.io.File; 
import java.io.FileNotFoundException;
import java.util.Scanner; 
import java.util.ArrayList;
public class A3
{
    // MAIN //
    public static void main(String[] args) throws Exception
    {
        // CLASS VARIABLES //

        int totalFrames = Integer.parseInt(args[0]);
        int timeSlice = Integer.parseInt(args[1]);
        ArrayList<Process> processList1 = new ArrayList<>();
        ArrayList<Process> processList2 = new ArrayList<>();

        // READ IN PROCESS FILES //

        ArrayList<Integer> pageRequests = new ArrayList<>();

        for(int i = 2; i < args.length; i++)
        {
            File file = new File(args[i]);
            String name = "";
            int id = 0;
            try(Scanner sc = new Scanner(file))
            {
                name = "";
                id = 0;
                pageRequests = new ArrayList<>();

                if(sc.hasNext("name:"))
                {
                    sc.next();
                    String nValue = sc.next();
                    name = nValue.substring(0, nValue.length() - 1);
                    id = Integer.parseInt(name.substring(7));
                }
            
                while(!sc.hasNext("end;"))
                {
                    if(sc.hasNext("page:"))
                    {
                        sc.next();
                        String pValue = sc.next();
                        int pageNumber = Integer.parseInt(pValue.substring(0, pValue.length() - 1));
                        pageRequests.add(pageNumber);
                    }
                }
                sc.close();
            }
            catch(FileNotFoundException e)
            {
                System.out.println("Incorrect Command Line Input.");
            }
            processList1.add(new Process(name, id, pageRequests));
            processList2.add(new Process(name, id, pageRequests));
        } 

        Scheduler scheduler1 = new Scheduler(processList1, timeSlice, new Memory(totalFrames), false);
        Scheduler scheduler2 = new Scheduler(processList2, timeSlice, new Memory(totalFrames), true);

        scheduler1.run();       //Static Allocation with Local Scope
        scheduler2.run();       //Variable Allocation with Global Scope 

        System.out.println(printResults(scheduler1));
        System.out.println("------------------------------------------------------------\n");
        System.out.println(printResults(scheduler2));
    }

    // STATIC METHODS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION:
    public static String printResults(Scheduler scheduler)
    {
        String results = "";
        String padding = "%-4s %-18s %-15s %-9s %s";

        results = (scheduler.isGlobal()) ? "LRU - Variable-Global Replacement:\n" : "LRU - Fixed-Local Replacement:\n";
        results += "PID  Process Name       Turnaround Time # Faults  Fault Times\n";

        for(Process process : scheduler.getFinishedQueue())
        {
            results += String.format(padding, process.getID(), process.getName(), process.getTurnTime(), process.getTotalPageFaults(), process.getFaultTimes()) + "\n";
        }

        return results;
    }
}