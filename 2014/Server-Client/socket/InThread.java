/*
 * Purpose: Monitor all servers and place the messages received
 *              into a TCPMessageQueue.
 * Author:  Alex Anderson
 * Date:    3/9/14
 */

package socket;

import java.io.IOException;
import java.util.ArrayList;

class InThread extends Thread
{
    public InThread(TCPMessageQueue queue, ArrayList<AddPort> sockInfo, int timeout)
    {
        this.queue = queue;
        this.sockInfo = sockInfo.toArray(new AddPort[sockInfo.size()]);
        this.timeout = timeout;
    }

    public void run()
    {
        try
        {
            this.initializeSockets(sockInfo);   //initialize all servers in sockInfo
            initialized = true;
        }
        catch(InterruptedException e)
        {
            System.out.println("The in thread was interrupted during initialization!");
            return;
        }

        int i = 0;      //keep index through timeouts to allow message checking to start in middle if needed
        while(true) //this thread will run as long as the main program (ideally)
        {
            try //for interrupt exceptions
            {
                long startTime = System.currentTimeMillis();
                int numMsgs = 0;    //track how many messages have been received
                //Check all sockets to see if there is anything in the buffers
                while(i < sockets.length)
                {
                    //if it wasn't initialized don't use it
                    if(sockets[i] == null)
                    {
                        System.out.println("WARNING: server " + sockInfo[i].name + " is null");
                        continue;
                    }

                    try
                    {
                        String protocol;
                        if((protocol = sockets[i].getMessage()) != null)  //if there is something there
                        {
                            String msg = sockets[i].getMessage();
                            //add to the queue
                            queue.add(new TCPMessage(sockets[i].getPortInfo().name, protocol, msg));
                            numMsgs++;  //increment the number of messages received this scan
                        }
                    }
                    catch(IOException e)
                    {
                        //so long as protocol is not null there should be no exceptions
                        //and if there are there isn't much I can do to fix it, so just go on to the next one
                        //TODO: if it happens at the same port consistently signal that the port might be down
                    }

                    /************Special exiting circumstances for flushing and timeouts***********/
                    if(System.currentTimeMillis() - startTime > timeout)
                        break;
                    if(i+1 >= sockets.length)      //just checked the last server look to see if we nee to go again
                    {
                        boolean goAgain = false;
                        //there is still time left, restart to check ports again
                        if(System.currentTimeMillis() - startTime < timeout)
                            goAgain = true;

                        //if a flush has been requested
                        if(queue.isFlushRequested())
                        {
                            //if messages were received we need to check if there are more
                            if(numMsgs > 0)
                                goAgain = true;
                            else
                                queue.flushComplete();
                        }

                        if(goAgain)
                        {
                            numMsgs = 0;
                            i = 0;
                        }
                    }
                    else
                        i++;
                }

                Thread.sleep(timeout);
            }
            catch(InterruptedException e)
            {
                System.out.println("The in thread was interrupted");
                return;
            }
        }
    }

    public synchronized boolean isInitialized()
    {
        return initialized;
    }

    //Could be depreciated
    private TCPServer getSocket(PortName name)
    {
        TCPServer sock = null;
        for(int i = 0; i < sockets.length; i++)
        {
            if(sockets[i].getPortInfo().name == name)
            {
                sock = sockets[i];
                break;
            }
        }
        return sock;
    }

    //returns the number of sockets successfully initialized
    private int initializeSockets(AddPort[] sockInfo) throws InterruptedException
    {
        int numInit = 0;    //number of sockets initialized successfully

        sockets = new TCPServer[sockInfo.length];    //use array to access faster than list
        //create servers
        for(int i = 0; i < sockets.length; i++)
        {
            //if the server hasn't been created yet
            if(sockets[i] == null)
            {
                try
                {
                    sockets[i] = new TCPServer(sockInfo[i], timeout/10);    //last parameter is the socket timeout value in milliseconds
                }
                catch(IOException e)
                {
                    //Exception was likely thrown because another server is already on this port
                    //This type of error cannot be resolved by the program
                    System.out.println("Error occurred creating server " + sockInfo[i].name);
                    //TODO: figure out a better way to resolve/signal these errors. Possibly throw custom exception
                    continue;   //continue to initialize of the next server
                }
            }

            //Server is already initialized. Now we wait for the client.
            try
            {
                sockets[i].waitForClient(); //waits for client for timeout in constructor
                numInit++;
            }
            catch(IOException e)
            {
                //No client showed up, sleep and check again
                // TODO: add in a give up to go to next server?
                //System.out.println("No client showed up on " + sockInfo[i].address + ":" + sockInfo[i].port);
                i--;    //reset i to come back to this server
                Thread.sleep(timeout/10);   //not for sure I need to sleep here
            }
        }


        System.out.format("%d input sockets initialization attempted. %d unsuccessful\n", sockets.length, sockets.length-numInit);

        return numInit;
    }

    private volatile boolean initialized = false;
    private int timeout;

    private TCPServer[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
