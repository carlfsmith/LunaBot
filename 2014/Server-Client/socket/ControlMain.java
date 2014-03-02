/*
 * Purpose: Demo use of SocketManger, TCPMessage, and
 *              TCPMessageQueue classes by the main thread
 * Author:  Alex Anderson
 * Date:    3/1/14
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

        //request information regarding the gyro's x-axis 100 times
        for(int i = 0; i < 100; i++)
            request.add(new TCPMessage(PortName.GYRO_IN, Protocol.xAxis, Protocol.request));

        SocketManager.getInstance().initAll(listen, request, 50);   //initialize the threads.
        SocketManager.getInstance().startAll();                     //start the threads
        SocketManager.getInstance().flushRequest();                 //flush all requests
        SocketManager.getInstance().flushListen();                  //listen until your ear falls off

        int size = listen.size();
        for(int i = 0; i < size; i++)       //print everything we heard
            System.out.println(listen.get());

        SocketManager.getInstance().interruptAll(); //stop the read/write threads
    }
}
