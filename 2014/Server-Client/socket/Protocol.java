/*
 * Purpose: Maintain the protocols used by the system to signal
 *              special events
 * Author:  Alex Anderson
 * Notes:   This is still under development. I plan to use a CSV
 *              file store the data.
 * Date: 2/15/14
 */

package socket;

public class Protocol
{
    public static final String empty = "~";
    public static final String request = "?";

    public static final String fileStart = "File";
    public static final String fileEnd = "eliF";

    public static final String xAxis = "x";
    public static final String yAxis = "y";
    public static final String zAxis = "z";
}
