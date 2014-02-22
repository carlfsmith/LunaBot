package socket;

/*
 * Purpose: Simple Queue which allows Listen/RequestThread classes
 *              to communicate with the main thread via the
 *              TCPMessage class.
 * Author:  Alex Anderson
 * Date:    2/22/14
 */

import java.util.ArrayList;

public class TCPMessageQueue
{
    public synchronized boolean add(TCPMessage message)
    {
        return msgs.add(message);   //add to the end of the list
    }
    public synchronized TCPMessage get()
    {
        try
        {
            TCPMessage msg = msgs.get(0);
            msgs.remove(0);
            return msg; //get from the beginning
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }
    public synchronized TCPMessage peek()
    {
        try
        {
            return msgs.get(0);
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }
    public synchronized int size()
    {
        return msgs.size();
    }

    private ArrayList<TCPMessage> msgs = new ArrayList<TCPMessage>();
}
