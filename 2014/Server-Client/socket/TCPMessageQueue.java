package socket;

/*
 * Purpose: Simple Queue which allows messenger threads
 *              to communicate with the main thread
 * Author:  Alex Anderson
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
        TCPMessage msg = msgs.get(0);
        msgs.remove(0);
        return msg; //get from the beginning
    }
    public synchronized int size()
    {
        return msgs.size();
    }

    private ArrayList<TCPMessage> msgs = new ArrayList<TCPMessage>();
}
