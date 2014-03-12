/*
 * Purpose: Demo use of SocketManger, TCPMessage, and
 *              TCPMessageQueue classes by the main thread
 * Author:  Alex Anderson
 * Notes:   Files can now be sent and received automatically.
 * Date:    3/11/14
 */

package socket;

import java.io.IOException;

public class ControlMain
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        long time0 = System.currentTimeMillis();
        int timeout = 50;   //milliseconds
        TCPMessageQueue in = new TCPMessageQueue();     //queue for listening for information
        TCPMessageQueue out = new TCPMessageQueue();    //queue for asking for information

        String recFileDir = "C:\\Users\\Alex\\Desktop\\socket test";    //directory where files will be stored when they are received, \\ are converted to / as needed
        SocketManager.getInstance().startAll(in, out, recFileDir, timeout);
        while(!SocketManager.getInstance().allReady())
            Thread.sleep(1);
        System.out.format("%dms for full startup\n", System.currentTimeMillis() - time0);

        //request data relating to the gyro's x-axis
        out.add(new TCPMessage(PortName.GYRO, Protocol.xAxis, Protocol.request));
        Thread.sleep(timeout * 2);   //give everything enough time to get through the pipe
        System.out.println("Request for data " + in.get());   //what a request for a data value looks like
        out.add(new TCPMessage(PortName.GYRO, Protocol.xAxis, 45)); //reply to request
        Thread.sleep(timeout*2);
        System.out.println("Reply to request " + in.get());   //what a reply looks like


        out.add(new TCPMessage(PortName.ACCEL, Protocol.fileRequest, Protocol.empty));  //request file of accelerator data
        Thread.sleep(timeout * 2);
        System.out.println("Request for file " + in.get());   //what a request for a file looks like
        out.add(new TCPMessage(PortName.ACCEL, Protocol.file, "port_map.csv")); //send file, port_map.csv, on the ACCEL port
        Thread.sleep(timeout*10);      //give more time for the whole file to be sent
        System.out.println("Size of in queue = " + in.size());  //queue should be empty file is written in predefined directories

        SocketManager.getInstance().interruptAll(); //stop the in/out threads
    }
}
