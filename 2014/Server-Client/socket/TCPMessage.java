/*
 * Purpose:     Contain the a message sent over the network
 * Author:      Alex Anderson
 * Notes:       Objects are immutable.
 * Date:        3/9/14
 */

package socket;

public class TCPMessage
{
    public TCPMessage(PortName name, String protocol, String message)
    {
        this.name = name;
        this.protocol = protocol;
        this.message = message;
    }
    public PortName getName()
    {
        return name;
    }
    public String getProtocol()
    {
        return protocol;
    }
    public String getMessage()
    {
        return message;
    }

    public String toString()
    {
        return "Message to/from " + name + ": " + protocol + " " + message;
    }

    private final PortName name;
    private final String protocol;
    private final String message;
}
