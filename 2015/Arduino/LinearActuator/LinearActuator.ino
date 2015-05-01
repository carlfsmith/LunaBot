//------------------------------------------------------
// This code was designed to control the direction of
// the bin and the excavator on the robot.
//------------------------------------------------------

#include <Wire.h>
#define ADDRESS 0x08

/*int  INA = A3;
int  INB = 10;
int  INH = A2;
int  IS = A1;
*/

byte trav_dist;
volatile char Command= '0';
char Previous_Command = '0';

// Set up for pins and to begin the i2c connection
void setup() {
	pinMode(A3, OUTPUT);
  	pinMode(10, OUTPUT);
	pinMode(A2, OUTPUT);
 	digitalWrite(A2, LOW);
	digitalWrite(A3, LOW);
  	digitalWrite(10, LOW);
  	Wire.begin(ADDRESS);
  	Wire.onReceive(receiveData);
  	Wire.onRequest(sendData);
}

// Main loop to delay
void loop() {
  	delayMicroseconds(10); 
}

// Function to receive data directly from Raspberry Pi
void receiveData(int byteCount){
	// While loop to run when there is communication from Pi
  	while(Wire.available()){
		// Read in first varibale sent from Pi into Command variable
		Command = char(Wire.read());
		// Read in second variable sent from Pi into PWM_val variable
    	trav_dist = int(Wire.read());
		// Make sure the trav_dist does not overflow or underflow
    	if(trav_dist > 255){
      	  trav_dist = 255;
    	}
		else if(trav_dist < 0){
			trav_dist =0;
		}
		// Call update function
    	update();
  	}  
}

// Function to send data back to Raspberry Pi if needed
void sendData(){
  	Wire.write(Previous_Command);
}

// Function that updates bin/excavator when command changes
void update(){
	// Chunk of code that moves bin/excavator up
	if(Command =='u'){
		digitalWrite(A2, LOW);
    	digitalWrite(A3, HIGH);
    	digitalWrite(10, LOW);
    	digitalWrite(A2, HIGH); //turn on in one direction
  	}
	// Chunk of code that moves bin/excavator down
 	else if(Command =='d'){
    	digitalWrite(A2, LOW);
    	digitalWrite(A3, LOW);
    	digitalWrite(10, HIGH);
    	digitalWrite(A2, HIGH);
  	}
	// Chunk of code that disables movement on bin/excavator
  	else if(Command ==' '){
    	digitalWrite(A2, LOW);
  	}
	// Sets current command to previous command
  	Previous_Command = Command;
}
