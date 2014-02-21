package socket;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alex on 2/20/14.
 */
class AskThread extends Thread
{
    public AskThread(TCPMessageQueue queue, ArrayList<AddPort> sockInfo)
    {
        this.queue = queue;
        this.sockInfo = sockInfo.toArray(new AddPort[sockInfo.size()]);
    }

    @Override
    public void run()
    {
        try
        {
            int numInit = this.initializeSockets(sockInfo);
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
                System.out.println("Hello, from the asking thread");
                while(queue.size() > 0)
                {
                    TCPMessage msg = queue.get();

                    MySocket sock = this.getSocket(msg.getName());
                    sock.sendMessage(msg.getProtocol());
                    sock.sendMessage(msg.getMessage());
                }
                Thread.sleep(1000);
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

    private MySocket[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
