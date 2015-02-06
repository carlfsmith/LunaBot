
package teleoperation;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread{
    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    DataOutputStream oStream = null;        
    DataInputStream iStream = null;
    String line = "";
    
    SocketServer(int port) {
        try { //Open Socket
            this.setName("SocketServerThread");
            this.start();
            serverSocket = new ServerSocket(port); 
        }        
        catch(IOException e) {
            System.out.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }
        
    public void AcceptClient() {
        try { //Open I/O streams
            clientSocket = serverSocket.accept();
            iStream = new DataInputStream(clientSocket.getInputStream());
            oStream = new DataOutputStream(clientSocket.getOutputStream());

        }          
        catch(IOException e) {
            System.out.println("Accept failed: 4321");
            System.exit(-1);
        }
    }

    public String nextLine() {
        try { //Read & write to I/O streams
            line = iStream.readLine();
        }
        catch(IOException e) {
            System.out.println(e);
        }

        return line;
    }
        
    public void sendText(String message) throws IOException {
        try {
        oStream.writeBytes(message + "\n");
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }    

    public void Disconnect() {
        try { //Close connections
            oStream.close();
            iStream.close();
            clientSocket.close();
            serverSocket.close();
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }
    
}