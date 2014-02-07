package server;

import java.io.*;

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
    }

    public String getMessage() throws IOException
    {
        if(fromClient != null)
            return fromClient.readLine();

        throw new IOException("TCPServer.fromClient is null");
    }

    public void writeCSVFile(String file_name) throws IOException
    {
        File f;
        if(file_name.endsWith(".csv"))
            f = new File(file_name);
        else
            f = new File(file_name + ".csv");

        BufferedWriter file = new BufferedWriter( new FileWriter( f.getAbsoluteFile() ) );

        final String end = "eliFVSC";
        String msg = "";

        while(true)
        {
            msg = getMessage();
            if(!msg.equalsIgnoreCase(end))
            {
                System.out.println(msg);
                file.write(msg);
                file.newLine();
            }
            else
                break;  //exit loop when the end string has been received
        }

        file.flush();
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