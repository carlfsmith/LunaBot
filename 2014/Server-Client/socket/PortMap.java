/*
 * Purpose: Maintain a map of sensors' IP addresses and ports
 * Author:  Alex Anderson
 * Notes:   This is still under development. I plan to use a CSV
 *              file store the data.
 * Date: 2/15/14
 */

package socket;

import java.util.ArrayList;

class PortMap
{
    public static PortMap getInstance()
    {
        return ourInstance;
    }

    public AddPort getAddPort(PortName name, String homeIP)
    {
        /* TODO: Look up IP address and port numbers
                    return "localhost" if the home and target IP addresses are the same
                    I'm not for sure the "localhost" thing is necessary
         */

        return new AddPort(name, "localhost", 3612);
    }

    private static PortMap ourInstance = new PortMap();
    private ArrayList<AddPort> ports = new ArrayList<AddPort>();
    private PortMap()
    {
        // TODO: Read IP addresses and port numbers from a file and store in "ports"
    }
}
