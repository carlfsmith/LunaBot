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
    public TCPMessage(PortName name, String protocol, int value)
    {
        this.name = name;
        this.protocol = protocol;
        this.message = Integer.toString(value);
    }
    public TCPMessage(PortName name, String protocol, double value)
    {
        this.name = name;
        this.protocol = protocol;
        this.message = Double.toString(value);
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
        return name + ": " + protocol + " " + message;
    }

    private final PortName name;
    private final String protocol;
    private final String message;
}
