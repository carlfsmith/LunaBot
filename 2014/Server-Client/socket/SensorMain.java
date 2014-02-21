package socket;

/**
 * Created by Alex on 2/20/14.
 */
public class SensorMain
{
    public static void main(String[] args)
    {
        TCPMessageQueue requestQueue = new TCPMessageQueue();
        TCPMessageQueue listenQueue = new TCPMessageQueue();
        SocketManager.getInstance().initialize(listenQueue, requestQueue);
    }
}
