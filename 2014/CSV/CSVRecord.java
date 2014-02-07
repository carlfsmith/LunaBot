/*
 *  Purpose: Stage values to be written to a CSV file
 *  Author: Alex Anderson
 *
 *  Note: The class must be given an instance of CSVFormat
 *          before it begins accepting values.
 */

package csv;

public class CSVRecord
{
    public CSVRecord(){}
    public CSVRecord(CSVFormat format)
    {
        setFormat(format);
    }
    //sets the format for this record
    public void setFormat(CSVFormat format)
    {
        this.format = format;
        items = new String[format.getSize()];

        for(int i = 0; i < items.length; i++)
            items[i] = "";
    }
    //gets the format for this record
    public CSVFormat getFormat()
    {
        return format;
    }
    //sets the value of an item based on the identifying string
    public boolean setItem(String id, String item)
    {
        if(format != null)  //a format has been defined
        {
            int location = format.getIndex(id);

            if(items != null)
            {
                items[location] = item;
                return true;
            }
        }
        return false;
    }
    //sets the value of an item based on the index
    public boolean setItem(int id, String item)
    {
        if(format != null)  //a format has been defined
        {
            if(0 <= id && id < items.length)    //this is a legal index in the array
            {
                items[id] = item;
                return true;
            }
        }
        return false;
    }
    //gets the value of an item based on the identifying string
    public String getItem(String id)
    {
        if(format != null)  //a format has been defined
        {
            int location = format.getIndex(id);

            if(location != -1)  //the item is in the list
            {
                return items[location];
            }
        }
        return null;
    }
    //gets the value of an item based on the index
    public String getItem(int id)
    {
        if(format != null)  //a format has been defined
        {
            if(0 <= id && id < items.length)    //this is a legal index in the array
                return items[id];
        }
        return null;
    }

    private String[] items = null;
    private CSVFormat format = null;
}
