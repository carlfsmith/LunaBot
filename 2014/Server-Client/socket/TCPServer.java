/*
 *  Purpose: Creates and maintains a TCP/IP connection to a client
 *  Author: Alex Anderson
 *
 *  Note:   As is, this only handles one client at a time; we may
 *              have to change this.
 *          This should run on the side least likely to fail
 *
 *  Date: 2/15/14
 */

package socket;

import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer
{
    public TCPServer(int port) throws IOException
    {
        createServer(port);
    }

    //returns false if the server has not been initialized or if the server is open
    public boolean createServer(int port) throws IOException
    {
        if(ss != null && ss.isClosed() == false)
            return false;
        else
        {
            ss = new ServerSocket(port);
            return true;
        }
    }

    public void waitForClient() throws IOException
    {
        client = ss.accept();
        fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        toClient = new DataOutputStream(client.getOutputStream());
    }

    public boolean sendMessage(String msg) throws IOException
    {
        if(toClient != null)
        {
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
        {
            String msg = fromClient.readLine();
            System.out.println("TCPServer.fromClient.readLine() = " + msg);
            return msg;
        }

        System.out.println("TCPServer.fromClient is null");
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
    private BufferedReader fromClient;
    private DataOutputStream toClient;
}