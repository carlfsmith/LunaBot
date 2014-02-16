/*
 * Purpose: Demonstrate the server side of the SocketManger class
 * Author:  Alex Anderson
 * Date: 2/15/14
 */

package socket;

import java.io.IOException;

public class ServerMain
{
    public static void main(String[] args)
    {
        SocketManager manager = new SocketManager();    //create manager

        try
        {
            manager.createServer(PortName.GYRO_X);  //create a server for the x-axis of the gyro
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.print("Server created\n");
        String msg = manager.getData(PortName.GYRO_X);  //get data from the x-axis of the gyro
        System.out.println("Recieved: " + msg + "\tSending: " + msg.toUpperCase());
        manager.sendData(PortName.GYRO_X, msg.toUpperCase());    //send data back to client on the same port
        System.out.println();

        msg = manager.getData(PortName.GYRO_X); //get data
        if( msg != null && msg.equals(Protocol.getInstance().fileStart()) ) //if the data signaled the start of a file transfer
        {
            System.out.println("Received file signal");
            manager.receiveFile(PortName.GYRO_X);   //receive and write a file from the x-axis of the gyro
            System.out.println("Finished file writing");
        }
        else
            System.out.println("Message was null or no file is being sent");
    }
}
