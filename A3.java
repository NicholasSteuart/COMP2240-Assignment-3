/* 
    * COMP2240 Assignment 3
    * File: A3.java
    * Author: Nicholas Steuart c3330826
    * Date Created: 27/9/24
    * Date Last Modified: 23/10/24
    * Description: MAIN file: Reads in the command line arguments specified in the Assignment specifications and Outputs the Summary of the simulation 
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

        int totalFrames = Integer.parseInt(args[0]);            //Total Frames that the simulation can use
        int timeSlice = Integer.parseInt(args[1]);              //The time quantum size or time slice that the Round Robin Scheduling Algorithm uses
        ArrayList<Process> processList1 = new ArrayList<>();    //To avoid Shallow Copies, 2 ArrayList<Process> are instantiated for a respective replacement strategies:
        ArrayList<Process> processList2 = new ArrayList<>();    //processList1: Static Allocation with Local Scope | processList2: Variable Allocation with Global Scope

        // READ IN PROCESS FILES //

        ArrayList<Integer> pageRequests = new ArrayList<>();    //Stores the Page requests of a Process file as it is read in

        for(int i = 2; i < args.length; i++)
        {
            File file = new File(args[i]);  //File being read in
            String name = "";               //Stores name of Process in File
            int id = 0;                     //Stores ID of Process in File

            //Attempt reading in the File
            try(Scanner sc = new Scanner(file))
            {
                //Set Process data to default values 
                name = "";
                id = 0;
                pageRequests = new ArrayList<>();

                //IF token read in is the Process name
                if(sc.hasNext("name:")) 
                {
                    sc.next();
                    String nameToken = sc.next();                                          //The name token read in from File
                    name = nameToken.substring(0, nameToken.length() - 1);      //Format and store the token into the format the Assignment Specified the name to be
                    id = Integer.parseInt(name.substring(7));                   //Format and store the ID of the Process
                }
            
                //Attempt reading in the page requests
                while(!sc.hasNext("end;"))
                {
                    //IF token read in is a Page in the File
                    if(sc.hasNext("page:"))
                    {
                        sc.next();
                        String pageNumberToken = sc.next();  //The Page number token read in from File
                        int pageNumber = Integer.parseInt(pageNumberToken.substring(0, pageNumberToken.length() - 1));  //Format and store the pageNumber
                        pageRequests.add(pageNumber);       //Add the Page to pageRequests list
                    }
                }

                sc.close(); //Close Scanner for the File
                //TODO: ASK NASIMUL ABOUT THE MAX 50 PAGE STIPULATION IN THE ASSIGNMENT SPECIFICATIONS
            }
            catch(FileNotFoundException e)
            {
                System.out.println("Incorrect Command Line Input. Command Line Arguments must be of the Form: java A3 F Q data1 data2 ... datan");
            }

            //Add the Process data as a deep copy to each list
            processList1.add(new Process(name, id, pageRequests));
            processList2.add(new Process(name, id, pageRequests));
        } 

        // INSTANTIATE SIMULATIONS //

        Scheduler scheduler1 = new Scheduler(processList1, timeSlice, new Memory(totalFrames), false);
        Scheduler scheduler2 = new Scheduler(processList2, timeSlice, new Memory(totalFrames), true);

        // RUN SIMULATIONS //

        scheduler1.run();       //Static Allocation with Local Scope
        scheduler2.run();       //Variable Allocation with Global Scope 

        // OUTPUT SUMMARY //
        System.out.println(printResults(scheduler1));
        System.out.println("------------------------------------------------------------\n");
        System.out.println(printResults(scheduler2));
    }

    // STATIC METHODS //

    //PRE-CONDITION: Parameter scheduler must be instantiated 
    //POST-CONDITION: String results returned containing the formatted summary of scheduler
    public static String printResults(Scheduler scheduler)
    {
        String results = "";                            //Stores the Summary results that will be returned
        String padding = "%-4s %-18s %-15s %-9s %s";    //Padding of the Process data, in line with the Assignment Specifications

        results = (scheduler.isGlobal()) ? "LRU - Variable-Global Replacement:\n" : "LRU - Fixed-Local Replacement:\n"; //Determines the heading of the scheduler's summary, based on the replacement strategy. The heading is then concatenated to results
        results += "PID  Process Name       Turnaround Time # Faults  Fault Times\n";                                   //The column headings of the Process data concatenated to results

        //Concatenates the Process data required in the summary (formatted by padding)
        for(Process process : scheduler.getFinishedQueue())
        {
            results += String.format(padding, process.getID(), process.getName(), process.getTurnTime(), process.getTotalPageFaults(), process.getFaultTimes()) + "\n";
        }

        return results; //Summary returned
    }
}