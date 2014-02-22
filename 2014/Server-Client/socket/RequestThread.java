/*
 * Purpose: Monitor the specified TCPMessageQueue and send any
 *              messages which are placed in it.
 * Author:  Alex Anderson
 * Notes:   This is functional, but still under construction
 * Date:    2/22/14
 */

package socket;

import java.io.IOException;
import java.util.ArrayList;

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
        catch (InterruptedException e)
        {
            System.out.println("Sudden death!");
            return;
        }

        while(true)
        {
            try
            {
                while(queue.size() > 0)
                {
                    TCPMessage msg = queue.get();

                    MySocket sock = this.getSocket(msg.getName());
                    sock.sendMessage(msg.getProtocol());
                    sock.sendMessage(msg.getMessage());
                    System.out.println("Requested something");
                }
                Thread.sleep(timeout);
            }
            catch(InterruptedException e)
            {
                System.out.println("The asking thread was interrupted");
                return;
            }
        }
    }

    private MySocket getSocket(PortName name)
    {
        MySocket sock = null;
        for(int i = 0; i < sockets.length; i++)
        {
            if(sockets[i].getName() == name)
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

        sockets = new MySocket[sockInfo.length];    //use array to access faster than list
        //create socket
        for(int i = 0; i < sockets.length; i++)
        {
            try
            {
                sockets[i] = new MySocket(sockInfo[i]);
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
    private MySocket[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
