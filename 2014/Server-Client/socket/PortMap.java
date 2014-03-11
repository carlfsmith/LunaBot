/*
 * Purpose: Read port_map.csv and compile information into AddPort objects
 * Author:  Alex Anderson
 * Notes:   Class expects to read CSV file port_map.csv to get data
 * Date:    2/22/14
 */

package socket;

import java.util.ArrayList;

import csv.CSVFileReader;
import csv.CSVRecord;

class PortMap
{
    public static PortMap getInstance()
    {
        return ourInstance;
    }

    public ArrayList<AddPort> getOutPorts()
    {
        return outPorts;
    }
    public ArrayList<AddPort> getInPorts()
    {
        return inPorts;
    }

    private static PortMap ourInstance = new PortMap();
    private ArrayList<AddPort> outPorts = new ArrayList<AddPort>();
    private ArrayList<AddPort> inPorts = new ArrayList<AddPort>();

    private PortMap()
    {
        CSVFileReader file = new CSVFileReader("port_map.csv"); //get the file
        CSVRecord record = file.readRecord();   //read the first record

        PortName[] portNames = PortName.values();   //array of recognized port names

        //while the read was valid
        while(record != null)
        {
            String name = record.getItem("Name");

            System.out.print("Record read: " + name);

            //see if the Name field matches one of the expected port names
            for(int i = 0; i < portNames.length; i++)
            {
                if(name.equalsIgnoreCase(portNames[i].name()))
                {
                    //If a match was found, get the address and port
                    String address = record.getItem("Address");
                    int port = Integer.parseInt(record.getItem("Port"));

                    //Determine if this is a server or client
                    boolean isServer = false;
                    if(record.getItem("Mode").equalsIgnoreCase("In"))
                        isServer = true;

                    //add a new AddPort object to store the information
                    if(isServer)
                        inPorts.add(new AddPort(portNames[i], address, port, isServer));
                    else
                        outPorts.add(new AddPort(portNames[i], address, port, isServer));

                    System.out.print(" at " + address + ":" + Integer.toString(port) + " Server=" + Boolean.toString(isServer) + "\tMatched");
                }
            }

            record = file.readRecord(); //read the next record
            System.out.println();
        }

        file.close();
    }
}
