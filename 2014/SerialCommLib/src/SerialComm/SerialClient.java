package SerialComm;

import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class SerialClient implements SerialPortEventListener {
    //passed from main GUI
    TestGUI window = null;

    //for containing the ports that will be found
    private Enumeration ports = null;
    
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private Sender output = null;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    private int timeOut = 2000;

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";

    public SerialClient(TestGUI window)
    {
        this.window = window;
        searchForPorts();
    }
    
    public SerialClient() {
        searchForPorts();
    }
    
    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI
    private void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                if(window == null){
                    System.out.println(curPort.getName());
                }
                else{
                    window.txtLog.append(curPort.getName() + "\n");
                }
                portMap.put(curPort.getName(), curPort);
                
                if(!ports.hasMoreElements())
                    connect(curPort.getName());
                
            }
        }
    }

    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect(String selectedPort) {
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);
        CommPort commPort;

        try {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open(this.getClass().getName(), timeOut);
            
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;

            setConnected(true);

            logText = selectedPort + " opened successfully.";
            if(window == null){
                    System.out.println(logText);
                }
                else{
                    window.txtLog.append(logText + "\n");
                }
        }
        catch (PortInUseException e) {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            if(window == null){
                    System.out.println(logText);
                }
                else{
                    window.txtLog.append(logText + "\n");
                }
        }
    }

    //open the input and output streams
    //pre: an open port
    //post: initialized intput and output streams for use to communicate data
    public boolean initIOStream() {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = new Sender(serialPort);
            
            successful = true;
            return successful;
        }
        catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            if(window == null){
                    System.out.println(logText);
                }
                else{
                    window.txtLog.append(logText + "\n");
                }
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener() {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            System.out.println("Listener started.");
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";
            if(window == null){
                    System.out.println(logText);
                }
                else{
                    //window.txtLog.setForeground(Color.RED);
                    window.txtLog.append(logText + "\n");
                }
        }
    }

    //disconnect the serial port
    //pre: an open serial port
    //post: clsoed serial port
    public void disconnect() {
        //close the serial port
        try {
            //writeData(0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);

            logText = "Disconnected.";
            if(window == null){
                        System.out.println(logText);
                    }
                    else{
                        window.txtLog.append(logText + "\n");
                    }
        }
        catch (IOException e) {
            logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
            if(window == null){
                    System.out.println(logText);
                }
                else{
                    window.txtLog.append(logText + "\n");
                }
        }
    }

    public boolean getConnected() {
        return bConnected;
    }

    public void setConnected(boolean bConnected) {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads
    @Override
    public void serialEvent(SerialPortEvent evt) {
        if(evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                byte singleData = (byte)input.read();
                
                logText = "";
                while(singleData != -1) { 
                    logText += new String(new byte[] {singleData});
                    singleData  = (byte)input.read();
                }
                
                if(window == null){
                    if(!logText.isEmpty())
                        System.out.print(logText);
                    System.out.println("Data Recieved.");
                }
                
                else{
                    window.txtLog.append(logText);
                }                
            }
            catch(IOException e) {
                logText = "Failed to read data. (" + e.toString() + ")";
                if(window == null){
                    System.out.println(logText);
                }
                else{
                    window.txtLog.append(logText + "\n");
                }
            }
        }
    }

    //method that can be called to send data
    //pre: open serial port
    //post: data sent to the other device
    public void writeData(int data) {
        try {
            output.writeData(data);
        }
        catch(IOException e) {
            logText = "Failed to write data. (" + e.toString() + ")";
            if(window == null){
                    System.out.println(logText);
                }
                else{
                    window.txtLog.append(logText + "\n");
                }
        }
    }
}

class Sender extends Thread {
    private SerialPort port = null;
    private OutputStream outbound = null;
    
    public Sender(SerialPort Port) throws IOException {
        port = Port;
        this.setName("SenderThread");
        this.start();
        outbound = port.getOutputStream();
    }
    
    public void writeData(int data) throws IOException {
        outbound.write(data);
        outbound.flush();
    }
    
    public void close()  throws IOException {
        outbound.close();
    }
}