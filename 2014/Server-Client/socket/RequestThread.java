/*
 * Purpose: Monitor the specified TCPMessageQueue and send any
 *              messages which are placed in it.
 * Author:  Alex Anderson
 * Notes:   This is functional, but still under construction
 * Date:    3/1/14
 */

package socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

class RequestThread extends Thread
{
    public RequestThread(TCPMessageQueue queue, ArrayList<AddPort> sockInfo, int timeout)
    {
        this.queue = queue;
        this.sockInfo = sockInfo.toArray(new AddPort[sockInfo.size()]);
        this.timeout = timeout;
    }

    @Override
    public void run()
    {
        try
        {
            this.initializeSockets(sockInfo);
        }
        catch(InterruptedException e)
        {
            System.out.println("Request thread was interrupted during initialization!");
            return;
        }

        while(true)
        {
            try
            {
                long startTime = System.currentTimeMillis();
                TCPMessage msg;
                TCPClient sock;
                //exit if we have exceeded execution time and a flush has not been requested
                while((System.currentTimeMillis() - startTime < timeout) || (queue.size() > 0 && queue.isFlushRequested()))
                {
                    //  there is a message in queue     the port send the message exists
                    if((msg = queue.get()) != null && ( sock = this.getSocket( msg.getName() )) != null )
                    {
                        try
                        {
                            sock.sendMessage(msg.getProtocol());
                            sock.sendMessage(msg.getMessage());
                        }
                        catch(IOException e)
                        {
                            //This exception should only be raised if the server disappeared
                            //TODO: possibly implement a way to detect if a server has left
                        }
                    }
                }

                //signal if flush was complete
                if(queue.isFlushRequested() && queue.size() == 0)
                    queue.flushComplete();

                Thread.sleep(timeout);
            }
            catch(InterruptedException e)
            {
                System.out.println("The request thread was interrupted");
                return;
            }
        }
    }

    private TCPClient getSocket(PortName name)
    {
        TCPClient sock = null;
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

        sockets = new TCPClient[sockInfo.length];    //use array to access faster than list
        //create socket
        for(int i = 0; i < sockets.length; i++)
        {
            try
            {
                sockets[i] = new TCPClient(sockInfo[i]);
                numInit++;
            }
            catch(IOException e)
            {
                //only reason for IOException is that the server wasn't up, so we'll try again
                System.out.println("Nothing was there for " + sockInfo[i].name.name());
                Thread.sleep(1000);  //wait for a second and try again
                i--;    //reset i
                continue;
            }
        }


        System.out.format("Requesting sockets initialization finished. %d unsuccessful\n", sockets.length-numInit);

        return numInit;
    }

    private int timeout;
    private TCPClient[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
