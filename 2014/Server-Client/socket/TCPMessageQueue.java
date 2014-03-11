/*
 * Purpose: Queue which allows Listen/RequestThread classes
 *              to communicate with the main thread via the
 *              TCPMessage class.
 * Author:  Alex Anderson
 * Notes:   Calling flush will block execution until complete.
 *              SocketManger's flush functions are preferred
 *              to setting flush directly although both should work.
 * Date:    3/1/14
 */

package socket;

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
    public synchronized void requestFlush()
    {
        requestFlush = true;
    }
    public synchronized void flushComplete()
    {
        requestFlush = false;
    }
    public synchronized boolean isFlushRequested()
    {
        return requestFlush;
    }
    public synchronized boolean isFlushComplete()
    {
        return !requestFlush;
    }

    private ArrayList<TCPMessage> msgs = new ArrayList<TCPMessage>();

    private boolean requestFlush = false;
}
