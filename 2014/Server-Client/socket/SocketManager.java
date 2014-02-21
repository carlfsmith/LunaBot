/*
 * Purpose: Singleton which allows creation of predefined sockets on two
 *              threads. One for listening another for asking. Communication
 *              with threads if facilitated by a MessageQueue class
 * Author:  Alex Anderson
 * Notes:   Uses PortName enumeration to specify which server/client to use
 *          I am considering making this class a singleton
 * Date: 2/18/14
 */

package socket;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

public class SocketManager
{
    private SocketManager(){}
    public static SocketManager getInstance()
    {
        return instance;
    }

    //Initializes threads accept read and write queues as parameters
    public boolean initialize(TCPMessageQueue listen, TCPMessageQueue ask)
    {
        askThread = new AskThread(ask, PortMap.getInstance().getAskPorts());

        askThread.start();
        return true;
    }

    public void interruptListen(){}
    public void interruptAsk()
    {
        askThread.interrupt();
    }
    public void interruptAll()
    {
        this.interruptAsk();
        this.interruptListen();
    }
    public void flushListen(){}   //ensure the queue is emptied, look up how to do this
    public void flushAsk(){}
    public void flushAll(){}

    private AskThread askThread;

    //lets the port map decide if it should be a server or not
    public boolean createSocket(PortName name) throws IOException
    {
        if(this.findSocket(name) == null)
        {
            AddPort portInfo = null;//PortMap.getInstance().getAddPort(name);

            sockets.add(new MySocket(portInfo));

            return true;
        }
        return false;
    }

    //returns true if successful
    public boolean sendData(PortName name, String protocol, String data)
    {
        boolean successful = false;
        MySocket sock = this.findSocket(name);
        if(sock != null)
        {
            if(sock.sendMessage(protocol))
                successful = sock.sendMessage(data);
        }

        return successful;
    }
    //returns null if an error occurred
    public String getData(PortName name)
    {
        MySocket sock = this.findSocket(name);
        if(sock != null)
        {
            System.out.println("socket found");
            return sock.getMessage();
        }

        return null;
    }

    //returns true if the file was sent successfully
    public boolean sendFile(PortName name, String file_name) throws FileNotFoundException
    {
        MySocket sock = this.findSocket(name);
        if(sock != null)
        {
            System.out.println("socket was found");
            return sock.sendFile(file_name);
        }

        return false;
    }
    //returns true if the file was received successfully
    public boolean receiveFile(PortName name)
    {
        try
        {
            MySocket sock = this.findSocket(name);
            if(sock != null)
            {
                sock.receiveFile();
                return true;
            }
        }
        catch (IOException e)
        {}

        return false;
    }
    //searches for a specific port which has already been initialized
    private MySocket findSocket(PortName name)
    {
        for(int i = 0; i < sockets.size(); i++)
        {
            if(name == sockets.get(i).getName())
                return sockets.get(i);
        }

        return null;
    }

    private static SocketManager instance = new SocketManager();
    private ArrayList<MySocket> sockets = new ArrayList<MySocket>();
}
