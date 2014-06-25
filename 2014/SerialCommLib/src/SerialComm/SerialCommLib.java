package SerialComm;

import java.util.Scanner;

public class SerialCommLib {
    public static void main(String[] args) throws InterruptedException{
        
        String payload;
        
        Scanner keyboard = new Scanner(System.in);
        SerialClient Arduino = new SerialClient();
        int exitValue = 0;
        
        if (Arduino.getConnected() == true)
        {
            if (Arduino.initIOStream() == true)
            {
                System.out.println("IO Stream started.");
                Arduino.initListener();
            }
        }
        System.out.println("Type exit to disconnect");
        
        while(exitValue == 0) {
            System.out.println("Please enter a value");
            payload = keyboard.nextLine();
            
            if(!payload.equalsIgnoreCase("exit")) {
                Arduino.writeData((int)payload.charAt(0));
                //Arduino.writeData('\n');
                Thread.sleep(70);
            }
            
            else {
                Arduino.writeData(0);
                exitValue = 1;
            }
        }
        
        Arduino.disconnect();
    }
}
