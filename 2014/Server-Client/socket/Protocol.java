/*
 * Purpose: Maintain the protocols used by the system to signal
 *              what kinds of data are needed and sent
 * Author:  Alex Anderson
 * Date:    5/1/14
 */

package socket;

public class Protocol
{
    //Special case message fields
    public static final String empty = "~";
    public static final String request = "?";

    //High level commands from Houston to the robot
    public static final String shutdown = ":...(";
    public static final String alive = ":)";
    public static final String forward = "go";
    public static final String backup = "come";
    public static final String lTurn = "lftTrn";
    public static final String rTurn = "rghtTrn";
    public static final String dig = "dig";         //can double as a state depending on the context
    public static final String dump = "dump";       //can double as a state depending on the context

    //robot status
    public static final String status = "status";
    public static final String clear = "clear";
    public static final String obFront = "obFront";


    //Signals for file transmission
    public static final String file = "file";   //signal that an out thread should send the file in message field
    public static final String fileStart = "fileSt";  //indicate the beginning of a file
    public static final String fileEnd = "fileEnd";    //indicate the end of a file
    public static final String fileRequest = "fileRq";  //request a file be sent to the sender
    public static final String fileLine = "fileLn"; //indicate a line from a file

    public static final String xAxis = "x";
    public static final String yAxis = "y";
    public static final String zAxis = "z";
}
