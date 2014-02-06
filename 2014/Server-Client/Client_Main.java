//Creates TCP Client at localhost:3612
//type, "take the highway" to exit

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client_Main
{
    public static void main(String argv[]) throws Exception
    {
        String sentence = "";
        String modifiedSentence = "";

        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

        TCPClient client = new TCPClient();

        boolean initClient = true;

        do
        {
            while(initClient == true)
            {
                String host = "localhost";
                int port = 3612;

                try
                {
                    client.connectTo(host, port);
                    initClient = false;
                    System.out.format("Connected to server at " + host + ":%d\n", port);
                }
                catch(IOException e)
                {
                    System.out.format("Waiting for server at " + host + ":%d\r", port);
                }
            }

            try
            {
                sentence = inFromUser.readLine();
                client.sendMessage(sentence);

                modifiedSentence = client.getMessage();
                System.out.println("FROM SERVER: " + modifiedSentence);
            }
            catch(IOException e)
            {
                System.out.println("Lost communication with the server\n");
                initClient = true;
            }
        }
        while(!sentence.equalsIgnoreCase("Take the highway"));

        client.close();
	}
}