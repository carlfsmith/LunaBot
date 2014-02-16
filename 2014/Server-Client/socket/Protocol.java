/*
 * Purpose: Maintain the protocols used by the system to signal
 *              special events
 * Author:  Alex Anderson
 * Notes:   This is still under development. I plan to use a CSV
 *              file store the data.
 * Date: 2/15/14
 */

package socket;

class Protocol
{
    public static Protocol getInstance()
    {
        return ourInstance;
    }

    public String fileStart()
    {
        return "File";
    }
    public String fileEnd()
    {
        return "eliF";
    }

    private Protocol()
    {
        // TODO: Read protocol from file
    }
    private static Protocol ourInstance = new Protocol();
}
