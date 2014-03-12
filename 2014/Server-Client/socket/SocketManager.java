/*
 * Purpose: Singleton which allows creation of predefined sockets on two
 *              threads. One thread for input another for output.
 *          Communication with threads if facilitated by a TCPMessageQueue class
 * Author:  Alex Anderson
 * Notes:   To specify which ports are to have servers/clients modify port_map.csv.
 *              if Mode=in a server will be created. if Mode=out a client
 *              will be created
 * Date:    3/7/14
 */

package socket;

public class SocketManager
{
    private SocketManager(){}
    public static SocketManager getInstance()
    {
        return instance;
    }

    //Initializes threads, accept read and write queues as parameters
    //timeout specifies how often ports/queues should be updated by the threads in milliseconds
    public boolean initOut(TCPMessageQueue out, int timeout)
    {
        outThread = new OutThread(out, PortMap.getInstance().getOutPorts(), timeout);
        outQueue = out;
        outTimeout = timeout;

        return true;
    }
    public boolean initIn(TCPMessageQueue in, String recFileDir, int timeout)
    {
        inThread = new InThread(in, PortMap.getInstance().getInPorts(), recFileDir, timeout);
        inQueue = in;
        inTimeout = timeout;

        return true;
    }
    public boolean initAll(TCPMessageQueue in, TCPMessageQueue out, String recFileDir, int timeout)
    {
        boolean areAllInit = initOut(out, timeout);
        areAllInit = initIn(in, recFileDir, timeout) && areAllInit;

        return areAllInit;
    }

    //returns false if out thread is not initialized
    public boolean startOut()
    {
        if(outThread == null)
            return false;

        if(!outThread.isAlive())
            outThread.start();

        return true;
    }
    public boolean startOut(TCPMessageQueue out, int timeout)
    {
        return initOut(out, timeout) && startOut();
    }
    //returns false if in thread is not initialized
    public boolean startIn()
    {
        if(inThread == null)
            return false;

        if(!inThread.isAlive())
            inThread.start();

        return true;
    }
    public boolean startIn(TCPMessageQueue in, String recFileDir, int timeout)
    {
        return initIn(in, recFileDir, timeout) && startIn();
    }
    //attempts to start the out and in threads
    //returns false if any thread problems occurred
    public boolean startAll()
    {
        boolean allStarted = startIn();
        allStarted = startOut() && allStarted;

        return allStarted;
    }
    public boolean startAll(TCPMessageQueue in, TCPMessageQueue out, String recFileDir, int timeout)
    {
        return initAll(in, out, recFileDir, timeout) && startAll();
    }

    //returns true if the thread has initialized
    public boolean outReady()
    {
        if(outThread != null)
            return outThread.isInitialized();

        return false;
    }
    //returns true if the thread has initialized
    public boolean inReady()
    {
        if(inThread != null)
            return inThread.isInitialized();

        return false;
    }
    //returns true if all threads have initialized
    public boolean allReady()
    {
        return outReady() && inReady();
    }

    public void interruptIn()
    {
        inThread.interrupt();
    }
    public void interruptOut()
    {
        outThread.interrupt();
    }
    public void interruptAll()
    {
        this.interruptOut();
        this.interruptIn();
    }

    //blocks until the all messages have been placed in the in queue
    //BE CAREFUL!!! Queue is considered flushed when there are no more messages on any of the ports, could possibly not return
    //TODO: Look into allowing flush on single port
    //return true if successful, false if interrupted or thread is uninitialized
    public boolean flushIn()
    {
        if(inReady())
        {
            inQueue.requestFlush();
            System.out.println("Flush requested on in queue.");

            try
            {
                int pause = inTimeout + (inTimeout/2);  //wait long enough for the thread to run at least once
                do
                {
                    Thread.sleep(pause);
                }
                while(!inQueue.isFlushComplete()); //continue to wait until the flush is done

                System.out.println("In queue flush successful? " + inQueue.isFlushComplete());

                return true;
            }
            catch(InterruptedException e)
            {
                System.out.println("Flushing of the in queue was interrupted.");
                return false;
            }
        }

        System.out.println("Flush on in queue could not be performed. Thread is not initialized.");
        return false;
    }
    //blocks until the out queue is empty
    //return true if successful, false if interrupted or thread is uninitialized
    public boolean flushOut()
    {
        if(outThread.isInitialized())
        {
            outQueue.requestFlush();
            System.out.println("Flush requested on out queue.");

            try
            {
                int pause = outTimeout + (outTimeout/2);  //wait long enough for the thread to run at least once
                do
                {
                    Thread.sleep(pause);
                }
                while(!outQueue.isFlushComplete()); //continue to wait until the flush is done

                System.out.println("Out queue successfully flushed? " + outQueue.isFlushComplete());

                return true;
            }
            catch (InterruptedException e)
            {
                System.out.println("Flushing of out queue was interrupted.");
            }
        }

        System.out.println("Flush on out queue could not be performed. Thread was not initialized");

        return false;
    }
    //blocks until both queues are flushed, out queue is flushed first
    //return true if successful, false if interrupted or thread is uninitialized
    public boolean flushAll()
    {
        boolean isFlushed = flushOut();
        isFlushed = flushIn() && isFlushed;

        return isFlushed;
    }

    private int outTimeout;
    private int inTimeout;

    private OutThread outThread;
    private InThread inThread;

    private TCPMessageQueue outQueue;
    private TCPMessageQueue inQueue;

    private static SocketManager instance = new SocketManager();
}
