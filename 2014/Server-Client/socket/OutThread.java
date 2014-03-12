/*
 * Purpose: Monitor the specified TCPMessageQueue and send any
 *              messages which are placed in it.
 * Author:  Alex Anderson
 * Date:    3/9/14
 */

package socket;

import java.io.IOException;
import java.util.ArrayList;

class OutThread extends Thread
{
    public OutThread(TCPMessageQueue queue, ArrayList<AddPort> sockInfo, int timeout)
    {
        this.queue = queue;
        this.sockInfo = sockInfo.toArray(new AddPort[sockInfo.size()]); //array is used for faster access
        this.timeout = timeout;
    }

    @Override
    public void run()
    {
        try
        {
            this.initializeSockets(sockInfo);
            initialized = true;
        }
        catch(InterruptedException e)
        {
            System.out.println("Out thread was interrupted during initialization!");
            return;
        }

        while(true) //run as long as the main thread
        {
            try
            {
                long startTime = System.currentTimeMillis();
                TCPMessage msg;
                TCPClient sock;
                //exit if        we have exceeded execution time        and    a flush has not been requested
                while((System.currentTimeMillis() - startTime < timeout) || (queue.size() > 0 && queue.isFlushRequested()))
                {
                    //  there is a message in queue     the requested port exists
                    if((msg = queue.get()) != null && ( sock = this.getSocket( msg.getName() )) != null )
                    {
                        try
                        {
                            String protocol = msg.getProtocol();    //get the message's protocol

                            if(protocol.equals(Protocol.file))   //a file needs to be sent
                            {
                                FileTag file = new FileTag(msg.getName(), msg.getMessage(), true);
                                if(file.isReady())  //the file was opened successfully
                                {
                                    queue.add(new TCPMessage(sock.getPortInfo().name, Protocol.fileStart, file.getFileName()));   //signal a file is about to be transmitted
                                    files.add(file);
                                }
                            }
                            else    //no special case, just send the message
                            {
                                //send a message
                                sock.sendMessage(protocol);
                                sock.sendMessage(msg.getMessage());

                                /******* if part of a file was sent put the next part on the queue ********/
                                //          part of a file                  just signaled the beginning
                                if(protocol.equals(Protocol.fileLine) || protocol.equals(Protocol.fileStart))  //part of a file was sent
                                {
                                    FileTag file = getFileTag(msg.getName());   //find the file's tag
                                    if(file != null)    //if the file's tag was found
                                    {
                                        if(file.isReady())    //if there is more to be read
                                            queue.add(new TCPMessage(file.getPortName(), Protocol.fileLine, file.getFileReader().readLine())); //send the next line
                                        else
                                        {
                                            queue.add(new TCPMessage(file.getPortName(), Protocol.fileEnd, file.getFileName()));    //signal that the end of the file has been reached
                                            file.getFileReader().close();   //close the file
                                            files.remove(file);     //remove the file from the list
                                        }
                                    }
                                }
                            }

                            sock.flush();   //ensure the message is sent
                        }
                        catch(IOException e)
                        {
                            //This exception should only be raised if the server disappeared
                            //TODO: possibly implement a way to detect if a server has left
                        }
                    }
                }

                //signal if flush was complete
                if(queue.isFlushRequested() && queue.size() == 0)
                    queue.flushComplete();

                Thread.sleep(timeout);
            }
            catch(InterruptedException e)
            {
                System.out.println("The out thread was interrupted");
                return;
            }
        }
    }

    public synchronized boolean isInitialized()
    {
        return initialized;
    }

    //find the client that is on the specified port
    private TCPClient getSocket(PortName name)
    {
        TCPClient sock = null;
        for(int i = 0; i < sockets.length; i++)
        {
            if(sockets[i].getPortInfo().name == name)
            {
                sock = sockets[i];
                break;
            }
        }
        return sock;
    }

    //find the FileTag which is being send on the specified port
    private FileTag getFileTag(PortName name)
    {
        for(int i = 0; i < files.size(); i++)
        {
            if(files.get(i).getPortName() == name)
                return files.get(i);
        }

        return null;
    }

    //returns the number of sockets successfully initialized
    private int initializeSockets(AddPort[] sockInfo) throws InterruptedException
    {
        int numInit = 0;    //number of sockets initialized successfully

        sockets = new TCPClient[sockInfo.length];    //use array to access faster than list
        //create socket
        for(int i = 0; i < sockets.length; i++)
        {
            try
            {
                sockets[i] = new TCPClient(sockInfo[i]);
                numInit++;
            }
            catch(IOException e)
            {
                //only reason for IOException is that the server wasn't up, so we'll try again
                //TODO: Possibly add a give up after so many tries
                System.out.println("Nothing was there for " + sockInfo[i].name.name());
                Thread.sleep(timeout/10);  //wait for a second and try again
                i--;    //reset i
                continue;
            }
        }


        System.out.format("%d output sockets initialization attempted. %d unsuccessful\n", sockets.length, sockets.length-numInit);

        return numInit;
    }

    private int timeout;
    private volatile boolean initialized = false;

    private ArrayList<FileTag> files = new ArrayList<FileTag>();

    private TCPClient[] sockets;
    private AddPort[] sockInfo;
    private TCPMessageQueue queue;
}
