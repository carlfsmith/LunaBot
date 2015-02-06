
package sockclient;

import java.io.IOException;
import java.util.Scanner;

public class ClientInterface {
    ClientInterface() throws IOException {
    
        SockClient clientSocket;
        Scanner keyboard = new Scanner(System.in);
        String message = "";
        String hostname;
        int port = 4321;
        
        System.out.println("Welcome to the AR9001 Teleoperation client.");
        System.out.println("Please enter the IP address of the AR9001.");
        System.out.print("IP Address: ");
        hostname = keyboard.nextLine();
        System.out.println("Initializing connection.");
        
        clientSocket = new SockClient(hostname, port);
        
        if(clientSocket.getConnectionStatus() == true) {
            System.out.println("Connection to AR9001 successfully established.");
            System.out.println("System is hot! Enter commands when ready!");
            while(!message.equalsIgnoreCase("exit")) {

                System.out.print("Command: ");
                message = keyboard.nextLine();
                clientSocket.writeBytes(message + "\n");   
                
                if(message.equalsIgnoreCase("power")) {                    
                    System.out.println(clientSocket.readString());
                }
                
                
            }

            clientSocket.close();
        }  
    }
}

