/*
 *  Purpose: Read a CSV file.
 *  Author: Alex Anderson
 *  Note: The class must be given a file name before it will read a file.
 *          The must end with .csv
 *          The first row is assumed to name the data in the columns
 *
 *  Date: 2/15/14
 */

package csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSVFileReader
{
    public CSVFileReader(){}
    public CSVFileReader(String file_name)
    {
        this.getFile(file_name);
    }

    public boolean getFile(String file_name)
    {
        if(file_name.endsWith(".csv"))
        {
            try
            {
                fromFile = new BufferedReader( new FileReader( new File(file_name) ) );
                return this.setCSVFormat(fromFile.readLine());
            }
            catch(IOException e)
            {
                return false;
            }
        }
        else
            return false;   //wrong file extension
    }

    public CSVFormat getFormat()
    {
        return format;
    }

    public CSVRecord readRecord()
    {
        if(fromFile == null)
            return null;    //a file has not be specified

        try
        {
            String[] items = fromFile.readLine().split(",");

            CSVRecord record = new CSVRecord(format);

            for(int i = 0; i < items.length; i++)
                record.setItem(i, items[i].trim());

            return record;
        }
        catch (IOException e)
        {
            return null;    //something went wrong with reading
        }
    }

    private boolean setCSVFormat(String line)
    {
        if(fromFile == null || !line.contains(","))
            return false;   //incorrect format for CSV

        String[] items = line.split(",");

        for(int i = 0; i < items.length; i++)
            format.addItem(items[i].trim());

        format.lockFormat();

        return true;
    }
    private BufferedReader fromFile = null;
    private CSVFormat format = new CSVFormat();;
}
