package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Alex on 2/20/14.
 */
public class ControlMain
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        TCPMessageQueue listen = new TCPMessageQueue(); //queue for listening for information
        TCPMessageQueue ask = new TCPMessageQueue();    //queue for asking for information
        SocketManager.getInstance().initialize(listen, ask);

        //request information regarding the gyro's x-axis
        ask.add(new TCPMessage(PortName.GYRO_IN, Protocol.xAxis, Protocol.request));


        System.out.println("Doing something useful");

		//check to see if there has been a response
        if(listen.size() > 0)
            System.out.println(listen.get().getMessage());

        BufferedReader userIn = new BufferedReader(new InputStreamReader( System.in ));
        while(!userIn.readLine().equalsIgnoreCase("stop"))
            Thread.sleep(1000);

        SocketManager.getInstance().interruptAll(); //stop the read/write threads
    }
}
