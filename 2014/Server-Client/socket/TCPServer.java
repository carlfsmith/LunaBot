/*
 *  Purpose: Creates and maintains a TCP/IP connection to a client
 *  Author:  Alex Anderson
 *
 *  Note:   Only handles one client at a time
 *          This should run on the side least likely to fail
 *
 *  Date: 2/15/14
 */

package socket;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

class TCPServer
{
    public TCPServer(AddPort portInfo) throws IOException
    {
        createServer(portInfo, 0);
    }
    public TCPServer(AddPort portInfo, int timeoutMills) throws IOException
    {
        createServer(portInfo, timeoutMills);
    }

    //returns false if the server has not been initialized or if the server is open
    private boolean createServer(AddPort portInfo, int timeoutMills) throws IOException
    {
        if(ss != null && ss.isClosed() == false)
            return false;
        else
        {
            ss = new ServerSocket(portInfo.port);
            setTimeout(timeoutMills);  //set timeout for waiting for clients
            this.portInfo = portInfo;
            return true;
        }
    }

    public void setTimeout(int timeoutMills) throws SocketException
    {
        if(ss != null)
            ss.setSoTimeout(timeoutMills);

        if(client != null)
            client.setSoTimeout(timeoutMills);

        this.timeoutMills = timeoutMills;
    }

    public void waitForClient() throws IOException
    {
        client = ss.accept();
        client.setSoTimeout(this.timeoutMills); //set timeout for socket
        fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        toClient = new DataOutputStream(client.getOutputStream());
    }

    public AddPort getPortInfo()
    {
        return this.portInfo;
    }

    public boolean sendMessage(String msg) throws IOException
    {
        if(toClient != null)
        {
            // \n signals the end of a message
            if(msg.endsWith("\n"))
                toClient.writeBytes(msg);
            else
                toClient.writeBytes(msg + "\n");

            return true;
        }

        return false;
    }

    public String getMessage() throws IOException
    {
        if(fromClient != null)
            return fromClient.readLine();

        return null;
    }

    public boolean flush()
    {
        try
        {
            toClient.flush();
        }
        catch (IOException e)
        {
            return false;
        }

        return true;
    }

    public void disconnectClient() throws IOException
    {
        client.close();
    }

    public void disconnectServer() throws IOException
    {
        disconnectClient();
        ss.close();
    }

    private ServerSocket ss;
    private Socket client;
    private int timeoutMills;
    private AddPort portInfo;
    private BufferedReader fromClient;
    private DataOutputStream toClient;
}