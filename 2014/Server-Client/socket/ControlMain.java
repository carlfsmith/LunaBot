/*
 * Purpose: Demo use of SocketManger, TCPMessage, and
 *              TCPMessageQueue classes by the main thread
 * Author:  Alex Anderson
 * Notes:   Files can be sent automatically but must be received manually.
 *              I plan to automate receiving files as well.
 *              The protocols used to signal file transfers are still in flux.
 * Date:    3/10/14
 */

package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ControlMain
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        long time0 = System.currentTimeMillis();
        int timeout = 50;   //milliseconds
        TCPMessageQueue in = new TCPMessageQueue();     //queue for listening for information
        TCPMessageQueue out = new TCPMessageQueue();    //queue for asking for information

        SocketManager.getInstance().startAll(in, out, timeout);
        while(!SocketManager.getInstance().allReady())
            Thread.sleep(1);
        System.out.format("%dms for full startup\n", System.currentTimeMillis() - time0);

        //request data relating to the gyro's x-axis
        out.add(new TCPMessage(PortName.GYRO, Protocol.xAxis, Protocol.request));

        Thread.sleep(50);   //give everything enough time to get through the pipe

        BufferedReader userIn = new BufferedReader( new InputStreamReader( System.in ) );
        boolean flag = true;
        while(!userIn.readLine().equalsIgnoreCase("exit"))  //type exit to exit other wise press enter to continue
        {
            System.out.println(in.get());
            Thread.sleep(50); //let threads do their thing
            if(flag)
                out.add(new TCPMessage(PortName.ACCEL, Protocol.fileRequest, "port_map.csv"));  //ensure that data can be sent/received during operations
            flag = false;
            System.out.println("Main is back");
        }

        SocketManager.getInstance().interruptAll(); //stop the in/out threads
    }
}
