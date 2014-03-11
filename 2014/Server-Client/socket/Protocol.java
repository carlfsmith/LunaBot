/*
 * Purpose: Maintain the protocols used by the system to signal
 *              what kinds of data are needed and sent
 * Author:  Alex Anderson
 * Notes:   Protocols for file transmission are still under development
 * Date:    3/9/14
 */

package socket;

public class Protocol
{
    //Special case message fields
    public static final String empty = "~";
    public static final String request = "?";

    //Signals for file transmission
    public static final String fileStart = "File";  //indicate the beginning of a file
    public static final String fileEnd = "eliF";    //indicate the end of a file
    public static final String fileRequest = "fileRq";  //request a file be sent
    public static final String fileLine = "fileLn"; //indicate a line from a file

    public static final String xAxis = "x";
    public static final String yAxis = "y";
    public static final String zAxis = "z";
}
