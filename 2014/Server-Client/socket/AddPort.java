/*
 * Purpose: Store information about a sensors' specific
 *              IP address and port
 *          Mainly used by the PortMap class
 * Author:  Alex Anderson
 * Date: 2/15/14
 */

package socket;

class AddPort
{
    public AddPort(PortName name, String address, int port)
    {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public final PortName name;
    public final String address;
    public final int port;
}
