/*
 * Purpose:     Maintain meta data about a file and allow access to it
 * Author:      Alex Anderson
 * Date:        3/9/14
 */

package socket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

class FileTag
{
    //portName = name of the port the file is to be sent on
    public FileTag(PortName portName, String path)
    {
        open(path);
        this.portName = portName;
    }

    public boolean open(String path)
    {
        try
        {
            File file = new File(path);
            this.fileName = file.getName();
            fileIn = new BufferedReader( new FileReader(file) );
            return true;
        }
        catch(FileNotFoundException e)
        {
            return false;
        }
    }

    public boolean isReady()
    {
        try
        {
            return fileIn.ready();
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public BufferedReader getFileIn()
    {
        return fileIn;
    }

    public PortName getPortName()
    {
        return portName;
    }

    public String getFileName()
    {
        return fileName;
    }

    private String fileName;
    private BufferedReader fileIn;
    private PortName portName;
}
