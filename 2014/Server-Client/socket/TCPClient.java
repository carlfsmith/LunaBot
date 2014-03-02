/*
 *  Purpose: Creates and maintains a TCP/IP connection to a server
 *  Author: Alex Anderson
 *
 *  Note: This should run on the side most likely to fail, as it
 *          can recover easier after a crash
 *
 *  Date: 3/1/14
 */

package socket;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;

class TCPClient
{
    public TCPClient(AddPort portInfo) throws IOException
    {
        connectTo(portInfo.address, portInfo.port);
        this.portInfo = portInfo;
    }

    //attempts to connect to a port on a given host i.e. localhost port 3333
    public boolean connectTo(String host, int port) throws IOException
    {
        //if this client is already connected to an existing host, notify the user
        //user should first call TCPClient.close() before TCPClient.connectTo()
        if(socket != null && socket.isClosed() == false)
            return false;
        else
        {
            socket = new Socket(host, port);
            toServer = new DataOutputStream(socket.getOutputStream());  //Output Stream to server
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //Input Stream from server
            return true;
        }
    }

    public AddPort getPortInfo()
    {
        return portInfo;
    }

    //Waits for a message to be sent from the server.
    //This suspends the Thread's execution.
    //Message's terminating character is \n
    public String getMessage() throws IOException
    {
        if(fromServer != null)
            return fromServer.readLine();

        return null;
    }

    //Sends a message to the server
    //Adds a \n at the end of the message if it doesn't exist
    public boolean sendMessage(String msg) throws IOException
    {
        if(toServer != null)
        {
            if(msg.endsWith("\n"))
                toServer.writeBytes(msg);
            else
                toServer.writeBytes(msg + "\n");

            return true;
        }

        return false;
    }

    public boolean flush()
    {
        try
        {
            toServer.flush();
        }
        catch (IOException e)
        {
            return false;
        }

        return true;
    }

    //Closes the socket and clears the I/O streams
    public void close() throws IOException
    {
        socket.close();
        toServer = null;
        fromServer = null;
    }

    private AddPort portInfo;
    private Socket socket;
    private DataOutputStream toServer;
    private BufferedReader fromServer;
}