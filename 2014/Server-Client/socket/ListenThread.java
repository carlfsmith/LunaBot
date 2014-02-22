/*
 * Purpose: Monitor all servers and place the messages received
 *              into a TCPMessageQueue.
 * Author:  Alex Anderson
 * Notes:   This is functional, but still under construction.
 * Date:    2/22/14
 */

package socket;

import java.io.IOException;
import java.util.ArrayList;

class ListenThread extends Thread
{
    public ListenThread(TCPMessageQueue queue, ArrayList<AddPort> sockInfo, int timeout)
    {
        this.queue = queue;
        this.sockInfo = sockInfo.toArray(new AddPort[sockInfo.size()]);
        this.timeout = timeout;
    }

    public void run()
    {
        try
        {
            this.initializeSockets(sockInfo);   //initialize all sockets/servers in sockInfo
        }
        catch(InterruptedException e)
        {
            System.out.println("Sudden death!");    //The thread was interrupted during initialization
            return;
        }

        while(true) //this thread will run as long as the main program (ideally)
        {
            try //for interrupt exceptions
            {
                //Check all sockets to see if there is anything in the buffers
                for(int i = 0; i < sockets.length; i++)
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
                        }
                    }
                    catch(IOException e)
                    {
                        //so long as protocol is not null there should be no exceptions
                    }
                }

                Thread.sleep(timeout);
            }
            catch(InterruptedException e)
            {
                System.out.println("The listening thread was interrupted");
                return;
            }
        }
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
        boolean createSuccess = false;
        for(int i = 0; i < sockets.length; i++)
        {
            //if the server hasn't been created yet
            if(createSuccess == false)
            {
                try
                {
                    sockets[i] = new TCPServer(sockInfo[i], 10);    //last parameter is the socket timeout value in milliseconds
                    createSuccess = true;
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
                sockets[i].waitForClient(); //waits for client for specified timeout
                numInit++;
                createSuccess = false;  //reset creation flag for next loop
            }
            catch(IOException e)
            {
                //No client showed up, sleep and check again
                System.out.println("No client showed up on " + sockInfo[i].address + ":" + sockInfo[i].port);
                i--;    //reset i to come bac to this server
                Thread.sleep(10);
            }
        }


        System.out.format("Listening sockets initialization finished. %d unsuccessful\n", sockets.length-numInit);

        return numInit;
    }

    private int timeout;
    private TCPServer[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
