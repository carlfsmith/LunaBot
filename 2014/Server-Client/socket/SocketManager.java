/*
 * Purpose: Singleton which allows creation of predefined sockets on two
 *              threads. One thread for listening another for requesting.
 *          Communication with threads if facilitated by a TCPMessageQueue class
 * Author:  Alex Anderson
 * Notes:   To specify which ports are to have servers/clients modify port_map.csv.
 *              if Role=listen a server will be created. if Role=request a client
 *              will be created
 * Date: 3/1/14
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
    public boolean initRequest(TCPMessageQueue request, int timeout)
    {
        requestThread = new RequestThread(request, PortMap.getInstance().getRequestPorts(), timeout);
        requestQueue = request;
        requestTimeout = timeout;
        return true;
    }
    public boolean initListen(TCPMessageQueue listen, int timeout)
    {
        listenThread = new ListenThread(listen, PortMap.getInstance().getListenPorts(), timeout);
        listenQueue = listen;
        listenTimeout = timeout;
        return true;
    }
    public boolean initAll(TCPMessageQueue listen, TCPMessageQueue request, int timeout)
    {
        return initRequest(request, timeout) && initListen(listen, timeout);
    }

    //returns false if request thread is not initialized
    public boolean startRequest()
    {
        if(requestThread == null)
            return false;

        requestThread.start();
        return true;
    }
    //returns false if listen thread is not initialized
    public boolean startListen()
    {
        if(listenThread == null)
            return false;

        listenThread.start();
        return true;
    }
    //returns false if any threads are not initialized
    public boolean startAll()
    {
        if(requestThread == null || listenThread == null)
            return false;

        startRequest();
        startListen();
        return true;
    }

    public void interruptListen()
    {
        listenThread.interrupt();
    }
    public void interruptRequest()
    {
        requestThread.interrupt();
    }
    public void interruptAll()
    {
        this.interruptRequest();
        this.interruptListen();
    }

    //blocks until the all messages have been placed in the listen queue
    //BE CAREFUL!!! Queue is considered flushed when there are no more messages on any of the ports
    //TODO: Look into allowing flush on single port
    //return true if successful
    public boolean flushListen()
    {
        listenQueue.requestFlush();
        System.out.println("Flush requested on listening queue.");

        try
        {
            int pause = listenTimeout + (listenTimeout/2);  //wait long enough for the thread to run at least once
            do
            {
                Thread.sleep(pause);
            }
            while(!listenQueue.isFlushComplete()); //continue to wait until the flush is done

            System.out.println("Listening queue flush successfull? " + listenQueue.isFlushComplete());

            return true;
        }
        catch(InterruptedException e)
        {
            System.out.println("Flushing of the listening queue was interrupted.");
            return false;
        }
    }
    //blocks until the request queue is empty
    //return true if successful
    public boolean flushRequest()
    {
        requestQueue.requestFlush();
        System.out.println("Flush requested on request queue.");

        try
        {
            int pause = requestTimeout + (requestTimeout/2);  //wait long enough for the thread to run at least once
            do
            {
                Thread.sleep(pause);
            }
            while(!requestQueue.isFlushComplete()); //continue to wait until the flush is done

            System.out.println("Request queue successfully flushed? " + requestQueue.isFlushComplete());

            return true;
        }
        catch (InterruptedException e)
        {
            System.out.println("Flushing of request queue was interrupted.");
            return false;
        }
    }
    //blocks until both queues are flushed, request queue is flushed first
    //return true if successful
    public boolean flushAll()
    {
        System.out.println("Dual flush was requested by main thread.");
        return this.flushRequest() && this.flushListen();
    }

    private int requestTimeout;
    private int listenTimeout;

    private RequestThread requestThread;
    private ListenThread listenThread;

    private TCPMessageQueue requestQueue;
    private TCPMessageQueue listenQueue;

    private static SocketManager instance = new SocketManager();
}
