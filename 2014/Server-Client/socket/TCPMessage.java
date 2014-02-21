package socket;

/**
 * Created by Alex on 2/20/14.
 */
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

    private final PortName name;
    private final String protocol;
    private final String message;
}
