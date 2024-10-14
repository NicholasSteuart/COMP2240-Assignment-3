/* File: Frame.java
* Author: Nicholas Steuart c3330826
* Date Created: 14/10/24
* Date Last Modified: 14/10/24
* Description: Implementation of a Frame in Memory
*/
public class Frame
{
    // CLASS VARIABLES //

    private int pageID;         //Page loaded into the Frame
    private int lruTime = 0;        //Stores the last time the Page was accessed
    private Process process;    //Stores the Process which currently owns the Page allocated to the Frame

    // CONSTRUCTOR //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Frame()
    {
        pageID = 0;
        process = null;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Frame(int pageID, Process process)
    {
        this.pageID = pageID;
        this.process = process;
    }
    // METHODS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void updateLRUTime()
    {
        lruTime++; 
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void resetLRUTime()
    {
        lruTime = 0;
    }
    
    // MUTATORS //
    
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setPageID(int pageID)
    {
        this.pageID = pageID;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public void setProcess(Process process)
    {
        this.process = process;
    }

    // ACCESSORS //

    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getPageID()
    {
        return pageID;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public int getLRUTime()
    {
        return lruTime;
    }
    //PRE-CONDITION: No pre-conditions
    //POST-CONDITION: 
    public Process getProcess()
    {
        return process;
    }
}
