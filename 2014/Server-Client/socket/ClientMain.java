/*
 * Purpose: Demonstrate the client side of the SocketManger class
 * Author:  Alex Anderson
 * Date: 2/15/14
 */

package socket;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ClientMain
{
    public static void main(String[] args)
    {
        SocketManager manager = new SocketManager();    //create manager

        try
        {
            manager.createClient(PortName.GYRO_X);  //create a client on the port for the x-axis of the gyro
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String msg = "hello";
        System.out.println("Sending: " + msg + "\tSuccess=" + manager.sendData(PortName.GYRO_X, msg));  //send gyro data and show debug data

        System.out.println("Received: " + manager.getData(PortName.GYRO_X) + "\n"); //get data from port

        try
        {
            System.out.println("Starting to send test.txt");
            manager.sendFile(PortName.GYRO_X, "test.txt");  //send the file test.txt over this port
            System.out.println("Sent file");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("file wasn't found");
        }
    }
}
