
package sockclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SockClient extends Thread{
    private Socket clientSocket = null;  
    private DataOutputStream os = null;
    private DataInputStream is = null;
    private Boolean Connected = false; 

    SockClient(String host, int port) {
        try {
        this.setName("SocketClientThread");
        this.start();
        clientSocket = new Socket(host, port);
        os = new DataOutputStream(clientSocket.getOutputStream());
        is = new DataInputStream(clientSocket.getInputStream());
        Connected = true;
        } 
        catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
        }
    }

    public Boolean getConnectionStatus() {
        return Connected;
    }

    public String readString() throws IOException {
     
        String message = is.readLine();
        //System.out.println("Log recieved");
        //System.out.println(message);
        return message;
    }
    
    public void writeBytes(String toSend) throws IOException {
        try {
            os.writeBytes(toSend);            
        }
        catch (IOException e) {
            System.err.println("IOException thrown by Socket client outbound stream method.");
        }
    }

    public void close() throws IOException {
        try {
            os.close();
            is.close();
            clientSocket.close();
            
            System.out.println("Socket Closed");
        } 
        catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } 
        catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}