/*
 * Purpose: Allow the user to respond to questions asked by the robot
 * Author:  Alex Anderson
 * Date:    5/1/14
 * Notes:   As it stands this is a passive interface until the robot
 *              sends a message indicating its state has changed. Only
 *              then is the user allowed to send a command to the robot.
 */

package houston;

import socket.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Houston
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        System.out.println("Starting up Houston...");

        Houston hq = new Houston();
        boolean exit = false;

        String prevState = Protocol.clear;
        while(exit == false)
        {
            //get robot state
            String currState = hq.getRobotState();

            //if the robot's state has changed
            if(currState != null && !prevState.equalsIgnoreCase(currState))
            {
                System.out.println("The robot's state has changed");

                //tell the user what happened
                if(currState.equalsIgnoreCase(Protocol.obFront))
                    System.out.println("There is an object in front the robot");
                else if(currState.equalsIgnoreCase(Protocol.clear))
                    System.out.println("The robot's state is clear");
                else
                    System.out.println("The robot is in an unrecognized state: " + currState);

                //ask the user what to do
                hq.requestUserCommand();
                Command command = hq.getUserCommand();

                //tell the robot what the user said
                hq.sendCommand(command);

                prevState = currState;
            }
        }

        //Tell robot to stop running
        hq.sendCommand(Command.SHUTDOWN);

        SocketManager.getInstance().interruptAll(); //stop the in/out threads

    }

    public Houston() throws InterruptedException
    {
        timeout = 50;
        msgIn = new TCPMessageQueue();
        msgOut = new TCPMessageQueue();

        String recFileDir = "";    //directory where files will be stored when they are received, \\ are converted to / as needed
        SocketManager.getInstance().startAll(msgIn, msgOut, recFileDir, timeout);
        while(!SocketManager.getInstance().allReady())
            Thread.sleep(1);
        System.out.println("Threads have been started");

        userIn = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println("Houston has been constructed");
    }

    //returns a string from the Protocol class or returns null if no status messages were found
    public String getRobotState() throws InterruptedException
    {
        LinkedList<TCPMessage> msgs =  new LinkedList<TCPMessage>();
        TCPMessage currMsg = null;

        //cycle through messages on the queue until the end is reach
        //or a message relating to the state is found
        boolean found = false;
        while(!msgIn.isEmpty())
        {
            currMsg = msgIn.get();
            if(currMsg.getProtocol().equalsIgnoreCase(Protocol.status))
            {
                found = true;
                break;
            }

            msgs.add(currMsg);
        }

        //if we removed any non-status messages from the queue, put them back on
        for(int i = 0; i < msgs.size(); i++)
            msgIn.add(msgs.get(i));

        if(found)
            return currMsg.getMessage();
        else
            return null;
    }

    //prints the command options for the user to choose
    public void requestUserCommand()
    {
        System.out.println("Which command should be sent to the robot?");
        System.out.println("\t0 : Shutdown");
        System.out.println("\t1 : Stop");
        System.out.println("\t2 : Nothing");
        System.out.println("\t3 : Go Forward");
        System.out.println("\t4 : Backup");
        System.out.println("\t5 : Turn left");
        System.out.println("\t6 : Turn right");
    }

    //returns which command the user selected from the menu
    public Command getUserCommand() throws IOException
    {
        int choice = -1;
        //if(userIn.ready())    //comment this out to allow the function to block execution
            choice = Integer.parseInt(userIn.readLine());

        switch(choice)
        {
            case 0: return Command.SHUTDOWN;
            case 1: return Command.STOP;
            case 2: return Command.NOTHING;
            case 3: return Command.FORWARD;
            case 4: return Command.BACKUP;
            case 5: return Command.TURN_LEFT;
            case 6: return Command.TURN_RIGHT;
            default: return Command.NOTHING;
        }
    }

    //sends a command to the robot
    //returns true if the robot responded
    public boolean sendCommand(Command command)
    {
        boolean successful = false;

        switch(command)
        {
            case FORWARD:
            {
                successful = sendMsgHandshake(new TCPMessage(PortName.ROBMAIN, Protocol.forward, Protocol.request));
                break;
            }
            case BACKUP:
            {
                successful = sendMsgHandshake(new TCPMessage(PortName.ROBMAIN, Protocol.backup, Protocol.request));
                break;
            }
            case TURN_LEFT:
            {
                successful = sendMsgHandshake(new TCPMessage(PortName.ROBMAIN, Protocol.lTurn, Protocol.request));
                break;
            }
            case TURN_RIGHT:
            {
                successful = sendMsgHandshake(new TCPMessage(PortName.ROBMAIN, Protocol.rTurn, Protocol.request));
                break;
            }
            case NOTHING:  //basically pinging the robot
            {
                successful = sendMsgHandshake(new TCPMessage(PortName.ROBMAIN, Protocol.empty, Protocol.empty));
                break;
            }
            default:    //default to shutting down the robot. default should never happen
            case SHUTDOWN:
            {
                successful = sendMsgHandshake(new TCPMessage(PortName.ROBMAIN, Protocol.shutdown, Protocol.request));
                break;
            }
        }

        return successful;
    }

    //just send the message and we don't care what gets returned
    public boolean sendMsgHandshake(TCPMessage msg)
    {
        try
        {
            if(sendMessage(msg, timeout*2) != null)
                return true;
        }
        catch(InterruptedException e)
        {}

        return false;
    }

    //send a message and return what the robot sent back
    //give up waiting after waiting prescribed amount of milliseconds
    public TCPMessage sendMessage(TCPMessage msg, int patience) throws InterruptedException
    {
        msgOut.add(msg);
        long time0 = System.currentTimeMillis();
        while(msgIn.isEmpty() && (System.currentTimeMillis()-time0) < patience)//wait for reply from robot
            Thread.sleep(10);

        return msgIn.get();
    }

    private int timeout;

    private BufferedReader userIn;

    private TCPMessageQueue msgIn;
    private TCPMessageQueue msgOut;

    private long robStTime = System.currentTimeMillis();
    private String robState;
}
