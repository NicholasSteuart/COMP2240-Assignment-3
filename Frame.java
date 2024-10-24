/* 
    * COMP2240 Assignment 3
    * File: Frame.java
    * Author: Nicholas Steuart c3330826
    * Date Created: 14/10/24
    * Date Last Modified: 24/10/24
    * Description: Implements the design and functionality of a Frame in main memory
*/
public class Frame
{
    // CLASS VARIABLES //

    private int pageID = 0;     //The ID of the Page loaded into the Frame
    private int lruTime = -1;    //The last time the Page was accessed. Default value is -1 since a Page could potentially be replaced at t = 0
    private Process process;    //The Process which the Page belongs to

    // CONSTRUCTOR //

    //PRE-CONDITION: No PRE-CONDITION
    //POST-CONDITION: Class variable Frame instantiated with default values
    public Frame()
    {
        process = null;
    }
    //PRE-CONDITION: Parameter process cannot be null
    //POST-CONDITION: Specialised Constructor instantiated with Parameters process having mutated Class variable process
    public Frame(Process process)
    {
        this.process = process;
    }
    
    // MUTATORS //
    
    //PRE-CONDITION: Frame Constructor instantiated and Parameter pageID must be greater than zero
    //POST-CONDITION: Class variable pageID mutated by parameter pageID
    public void setPageID(int pageID)
    {
        this.pageID = pageID;
    }
    //PRE-CONDITION: Frame Constructor instantiated and Parameter lruTime must be a non-negative Integer
    //POST-CONDITION: Class variable lruTime mutated by parameter lruTime
    public void setLRUTime(int lruTime)
    {
        this.lruTime = lruTime;
    }
    //PRE-CONDITION: Frame Constructor instantiated and Parameter process must not be null
    //POST-CONDITION: Class variable process mutated by parameter process
    public void setProcess(Process process)
    {
        this.process = process;
    }

    // ACCESSORS //

    //PRE-CONDITION: Frame Constructor instantiated and pageID must be greater than 0
    //POST-CONDITION: Class variable pageID returned
    public int getPageID()
    {
        return pageID;
    }
    //PRE-CONDITION: Frame Constructor instantiated and lruTime must be a non-negative Integer
    //POST-CONDITION: Class variable lruTime returned
    public int getLRUTime()
    {
        return lruTime;
    }
    //PRE-CONDITION: Frame Constructor instantiated and process must not be null
    //POST-CONDITION: Class variable process returned 
    public Process getProcess()
    {
        return process;
    }
}
