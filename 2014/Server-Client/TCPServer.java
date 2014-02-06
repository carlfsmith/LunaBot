//Creates and maintains a TCP Server
//at a given port

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class TCPServer
{
    public TCPServer(){}
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

    public void sendMessage(String msg) throws IOException
    {
        if(toClient != null)
            toClient.writeBytes(msg);

        //throw new IOException("TCPServer.toClient is null");
    }

    public String getMessage() throws IOException
    {
        if(fromClient != null)
            return fromClient.readLine();

        throw new IOException("TCPServer.fromClient is null");
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