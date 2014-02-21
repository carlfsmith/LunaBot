/*
 * Purpose: Streamline the creation of servers/clients
 *          Use AddPort objects to ensure servers/clients
 *          communicate on the correct ports.
 * Author: Alex Anderson
 *
 * Date: 2/18/14
 */

package socket;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

class MySocket
{
    public MySocket(AddPort portInfo) throws IOException
    {
        this.init(portInfo);
    }
    private void init(AddPort portInfo) throws IOException
    {
        this.sockInfo = portInfo;

        if(portInfo.isServer)
        {
            TCPServer temp = new TCPServer(portInfo.port);
            temp.waitForClient();
            server = temp;
        }
        else
            client = new TCPClient(portInfo.address, portInfo.port);
    }

    public PortName getName()
    {
        return sockInfo.name;
    }

    public boolean isServer()
    {
        return sockInfo.isServer;
    }

    public boolean sendMessage(String msg)
    {
        try
        {
            if(isServer())
                return server.sendMessage(msg);
            else
                return client.sendMessage(msg);
        }
        catch(IOException e)
        {
            return false;
        }
    }
    //returns null if an error occurred
    public String getMessage()
    {
        try
        {
            if(this.isServer())
            {
                System.out.println("I'm a server");
                return server.getMessage();
            }
            else
            {
                System.out.println("I'm a client");
                return client.getMessage();
            }
        }
        catch(IOException e)
        {
            System.out.println("IOException in MySocket.getMessage()");
            e.printStackTrace();
            return null;
        }
    }

    //sends the specified file
    public boolean sendFile(String file_name) throws FileNotFoundException
    {
        File f = new File(file_name);

        if(f.exists() && !f.isDirectory() && f.canRead())
        {
            boolean successful = true;
            BufferedReader inFile = new BufferedReader(new FileReader(f));
            this.sendMessage(Protocol.fileStart);
            this.sendMessage(file_name);

            String line = "";
            do
            {
                try
                {
                    line = inFile.readLine();
                    if(line != null)
                        this.sendMessage(line);
                }
                catch (IOException e)
                {
                    //something went wrong reading the file
                    //signal that we are done sending data and return false
                    successful = false;
                    break;
                }
            }
            while(line != null);

            this.sendMessage(Protocol.fileEnd);
            this.flush();
            return successful;
        }

        //Either the file doesn't exist, is a directory, or the permissions are wrong
        return false;
    }

    //receives and writes a file under the name that was sent
    //TODO: Allow the location where the file is saved to be controlled
    public void receiveFile() throws IOException
    {
        String file_name = this.getMessage();
        BufferedWriter outFile = new BufferedWriter( new FileWriter( new File(file_name) ) );

        //Assume the send file signal has already been sent
        String msg = this.getMessage();
        while( !msg.equals(Protocol.fileEnd) )
        {
            outFile.write(msg);
            outFile.newLine();
            msg = this.getMessage();
        }

        outFile.flush();
    }

    public boolean flush()
    {
        if(isServer())
            return server.flush();
        else
            return client.flush();
    }

    private AddPort sockInfo;
    private TCPServer server;
    private TCPClient client;
}
