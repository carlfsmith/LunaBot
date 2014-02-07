/*
 *  Purpose: Create and write a CSV file.
 *  Author: Alex Anderson
 *  Note: The class must be given a file name and a CSVFormat object before it will
 *              begin writing to the file.
 *  Date: 2/5/14
 */

package csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CSVFile
{
    public CSVFile(){}
    public CSVFile(CSVFormat format)
    {
        setFormat(format);
    }
    public CSVFile(String file_name)
    {
        setFile(file_name);
    }
    public CSVFile(String file_name, CSVFormat format)
    {
        setFormat(format);
        setFile(file_name);
    }

    public boolean setFormat(CSVFormat format)
    {
        //if the header for the file has been created it is too late to change the format
        if(wroteHeader == false)
        {
            this.format = format;
            return true;
        }

        return false;
    }
    public boolean setFile(String file_name)
    {
        File f;
        if(file_name.endsWith(".csv"))
            f = new File(file_name);
        else
            f = new File(file_name + ".csv");

        try
        {
            file = new BufferedWriter( new FileWriter( f.getAbsoluteFile() ) );
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public boolean writeRecord(CSVRecord record) throws IOException
    {
        //if the file write stream has been initialized and a format has been defined
        if(file != null && format != null)
        {
            if(wroteHeader == false)
            {
                writeHeader();
                wroteHeader = true;
            }

            //Attempt to write the information in this record
            if(record.getFormat() == format)    //This record has the same format as the file
            {
                for(int i = 0; i < format.getSize();i++)    //write all items in record
                {
                    String data = record.getItem(i);

                    if(data.contains(","))  //Surround item in " " if it contains commas
                        data = "\"" + data + "\"";

                    //Don't put a comma at the very end
                    if(i+1 != format.getSize())
                        file.write(data + ",");
                    else
                        file.write(data);
                }

                file.newLine();
                file.flush();

                return true;
            }
        }
        return false;

    }
    private void writeHeader() throws IOException
    {
        //Write the title/id/headers of each column from the format
        if(wroteHeader == false)
        {
            for(int i = 0; i < format.getSize(); i++)
            {
                String data = format.getID(i);

                if(data.contains(","))  //Surround item in " " if it contains commas
                    data = "\"" + data + "\"";

                //Don't put a comma at the very end
                if(i+1 != format.getSize())
                    file.write(data + ",");
                else
                    file.write(data);
            }

            file.newLine();
            file.flush();

            wroteHeader = true;
        }
    }

    private boolean wroteHeader = false;
    private CSVFormat format = null;
    private BufferedWriter file = null;
}