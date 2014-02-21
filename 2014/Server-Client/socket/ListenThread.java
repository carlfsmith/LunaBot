package socket;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alex on 2/20/14.
 */
public class ListenThread extends Thread
{
    public ListenThread(TCPMessageQueue queue, ArrayList<AddPort> sockInfo)
    {
        this.queue = queue;
        this.sockInfo = sockInfo.toArray(new AddPort[sockInfo.size()]);
    }

    public void run()
    {
        try
        {
            this.initializeSockets(sockInfo);
        }
        catch(InterruptedException e)
        {
            System.out.println("Sudden death!");
            return;
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
        //create sockets
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
                System.out.println("Nothing was there there for " + sockInfo[i].name.name());
                Thread.sleep(1000);  //wait for a second and try again
                i--;    //reset i
                continue;
            }
        }


        System.out.format("Listening sockets initialization finished. %d unsuccessful\n", sockets.length-numInit);

        return numInit;
    }

    private MySocket[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
