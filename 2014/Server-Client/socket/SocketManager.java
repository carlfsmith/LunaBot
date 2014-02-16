/*
 * Purpose: Allows for a single interface to all servers/clients
 * Author:  Alex Anderson
 * Notes:   Uses PortName enumeration to specify which server/client to use
 *          I am considering making this class a singleton
 * Date: 2/15/14
 */

package socket;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

public class SocketManager
{
    public SocketManager(){}

    //returns false if server could not be created
    public boolean createServer(PortName name) throws IOException
    {
        //if a server is already created for this, don't add another
        //only one server is allowed on a port
        if(this.findSocket(name) == null)
        {
            sockets.add(new MySocket(name, "localhost", true));
            return true;
        }

        return false;
    }
    //returns false if server could not be created
    public boolean createClient(PortName name) throws IOException
    {
        if(this.findSocket(name) == null)
        {
            sockets.add(new MySocket(name, "localhost", false));
            return true;
        }

        return false;
    }

    //returns true if successful
    public boolean sendData(PortName name, String data)
    {
        MySocket sock = this.findSocket(name);
        if(sock != null)
            return sock.sendMessage(data);  //returns true if successful

        return false;   //Socket wasn't defined for that port
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

    private ArrayList<MySocket> sockets = new ArrayList<MySocket>();
}
