package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Alex on 2/1/14.
 */
public class Main
{
    public static void main(String argv[]) throws Exception
    {
        String clientSentence = "";
        String capitalizedSentence = "";
        TCPServer tcpServer = new TCPServer();

        boolean initServer = true;

        do
        {
            if(initServer == true)
            {
                System.out.println("Waiting for clients");

                tcpServer.createServer(3612);
                tcpServer.waitForClient();

                System.out.println("Connection was accepted");
                initServer = false;
            }

            try
            {
                System.out.println("Waiting for client's message");
                clientSentence = tcpServer.getMessage();
                System.out.println("Received: " + clientSentence);
                if(clientSentence.equalsIgnoreCase("file"))
                {
                    tcpServer.writeCSVFile("yo");
                }
                else
                {
                    capitalizedSentence = clientSentence.toUpperCase() + '\n';
                    System.out.print("Sending data to client...");
                    tcpServer.sendMessage(capitalizedSentence);
                    System.out.print("Sucess!\n");
                }
            }
            catch(IOException e)
            {
                System.out.println("Something broke up the relationship!");
                System.out.println("Attempting to begin talking again!");
                initServer = true;
            }
        }
        while(!clientSentence.equalsIgnoreCase("Take the highway"));

        tcpServer.disconnectServer();
    }
}
