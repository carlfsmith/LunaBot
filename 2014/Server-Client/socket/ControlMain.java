/*
 * Purpose: Demo use of SocketManger, TCPMessage, and
 *              TCPMessageQueue classes by the main thread
 * Author:  Alex Anderson
 * Date:    2/22/14
 */

package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ControlMain
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        TCPMessageQueue listen = new TCPMessageQueue(); //queue for listening for information
        TCPMessageQueue request = new TCPMessageQueue();    //queue for asking for information
        SocketManager.getInstance().initialize(listen, request, 500);   //initialize/start the threads. Could make this two functions

        //request information regarding the gyro's x-axis
        request.add(new TCPMessage(PortName.GYRO_IN, Protocol.xAxis, Protocol.request));

        BufferedReader userIn = new BufferedReader(new InputStreamReader( System.in ));
        while(!userIn.readLine().equalsIgnoreCase("stop"))  //Press enter to generate any input .readLine() blocks until \n
        {
            System.out.println("Do something useful");
            if(listen.peek() != null)
                System.out.println(listen.get().getMessage());

            System.out.println(listen.size());

            Thread.sleep(1000);
        }

        SocketManager.getInstance().interruptAll(); //stop the read/write threads
    }
}
