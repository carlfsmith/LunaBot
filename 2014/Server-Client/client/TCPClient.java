/*
 *  Purpose: Creates and maintains a TCP/IP connection to a server
 *  Author: Alex Anderson
 *
 *  Note: This should run on the robot, as it can recover easier
 *          after a crash
 *        The majority of the methods throw exceptions. I figured
 *          that the main program would know better how to handle
 *          these.
 *
 *  Date: 2/5/14
 */

package client;

import java.io.*;
import java.net.Socket;

class TCPClient
{
    public TCPClient(){}
    public TCPClient(String host, int port) throws IOException
    {
        connectTo(host, port);
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

    public void sendFile(String file_name) throws IOException
    {
        //Attempt to open file return false if unsuccessful
        File file = new File(file_name);
        BufferedReader fileRead = new BufferedReader(new FileReader(file));
        System.out.println("Attempting to send file: " + file_name);

        sendMessage("File");  //Signal that we are about to send a file

        while(file.exists() && !file.isDirectory())
        {
            try
            {
                String line = fileRead.readLine();
                if(line != null)
                    sendMessage(line);
                else
                    break;  //exit loop when there is no more data
            }
            catch (IOException e)
            {
                System.out.println("I/O Exception");
            }
        }
        System.out.println("Done writing");
        sendMessage("eliFVSC");
    }

    //Closes the socket and clears the I/O streams
    public void close() throws IOException
    {
        socket.close();
        toServer = null;
        fromServer = null;
    }

    private Socket socket;
    private DataOutputStream toServer;
    private BufferedReader fromServer;
}