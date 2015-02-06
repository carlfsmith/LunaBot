
package teleoperation;

import java.io.IOException;

public class TeleOperation {
    
    public static void main(String[] args) throws InterruptedException{            
        String payload = "";      
        SerialClient Arduino = new SerialClient();
        SocketServer Server = new SocketServer(4321);
        int exitValue = 0;
        
        if (Arduino.getConnected() == true) {
            if (Arduino.initIOStream() == true) {
                System.out.println("IO Stream started.");
                Arduino.initListener();
            }
        }
        
        Server.AcceptClient();
        System.out.println("Server started.");
        
        while(exitValue == 0) {
            payload = Server.nextLine();
            
            if(!payload.equalsIgnoreCase("exit")) {
                Arduino.writeData((int)payload.charAt(0));
                System.out.println(payload);
                
            }
            
            else {
                if(Arduino.getConnected() == true)
                    Arduino.writeData(0);
                exitValue = 1;
            }
            
            if(payload.equalsIgnoreCase("power")) {
                try {
                    Thread.sleep(1000);
                    String current = Arduino.readData();                    
                    Server.sendText(current);
                }
                catch(IOException e) {
                     System.out.println(e);
                }
                //System.out.println("test: " + Arduino.readData());
            }
        }
        
        Server.Disconnect();
        if(Arduino.getConnected() == true)
            Arduino.disconnect();
        
    }
}
    

