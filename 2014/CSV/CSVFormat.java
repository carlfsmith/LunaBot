/*
 *  Purpose: Defines format of CSV columns i.e. which values go
 *              in which columns etc.
 *  Author: Alex Anderson
 *
 *  Date: 2/5/14
 */

import java.util.ArrayList;

public class CSVFormat
{
    public CSVFormat()
    {
        format = new ArrayList<String>();
        formatLocked = false;
    }
    //adds an item to list
    public boolean addItem(String item)
    {
        if(formatLocked == false)
        {
            format.add(item);
            return true;
        }

        return false;
    }
    //locks the format, disallowing additions/modifications
    public void lockFormat()
    {
        formatLocked = true;
    }

    public boolean contains(String id)
    {
        for(int i = 0; i < format.size(); i++)
        {
            if(format.get(i).equalsIgnoreCase(id))
                return true;
        }

        return false;
    }

    public int getIndex(String id)
    {
        return format.indexOf(id);
    }

    public String getID(int index)
    {
        if(0 <= index && index < format.size())
            return format.get(index);

        return null;
    }

    public int getSize()
    {
        return format.size();
    }

    private boolean formatLocked;
    private ArrayList<String> format;
}
