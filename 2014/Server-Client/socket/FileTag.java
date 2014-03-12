/*
 * Purpose:     Maintain meta data about a file and allow access to it
 * Author:      Alex Anderson
 * Date:        3/11/14
 */

package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

class FileTag
{
    //portName = name of the port the file is to be sent on
    public FileTag(PortName portName, String path, boolean read)
    {
        open(path, read);
        this.read = read;
        this.portName = portName;
    }

    public boolean open(String path, boolean read)
    {
        try
        {
            File file = new File(path);
            this.fileName = file.getName();

            if(read)
                fileReader = new BufferedReader( new FileReader(file) );
            else
            {
                if(!file.exists())
                    file.createNewFile();
                fileWriter = new BufferedWriter( new FileWriter(file) );
            }
            return true;
        }
        catch(IOException e)
        {
            return false;
        }
    }

    public boolean isReady()
    {
        try
        {
            if(read)
                return fileReader.ready();
            else
                return (fileWriter != null);
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public BufferedReader getFileReader()
    {
        return fileReader;
    }

    public BufferedWriter getFileWriter()
    {
        return fileWriter;
    }

    public PortName getPortName()
    {
        return portName;
    }

    public String getFileName()
    {
        return fileName;
    }

    private boolean read;
    private String fileName;
    private BufferedReader fileReader;
    private BufferedWriter fileWriter;
    private PortName portName;
}
