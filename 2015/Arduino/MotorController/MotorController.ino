//------------------------------------------------------
// This code was designed to directly control the
// rotation and speed of the excavator and wheels 
// on the robot.
//------------------------------------------------------

#include <Wire.h>
#define ADDRESS 0x04

// Variables with pin locations to keep organized
int FOR = 9;
int REV = 10;
int DIS = 8;
byte PWM_val = 150;
volatile char Command = '0';
char Previous_Command = '0';

// Set up for pins and to begin the i2c connection
void setup() {
  pinMode(FOR,OUTPUT);
  pinMode(REV,OUTPUT);
  pinMode(DIS,OUTPUT);
  digitalWrite(DIS,HIGH);
  Wire.begin(ADDRESS);
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);
}

// Main loop to delay
void loop(){
    delayMicroseconds(50);
}

// Function to receive data directly from Raspberry Pi
void receiveData(int byteCount){
	// While loop to run when there is communication from Pi
	while(Wire.available()){
		// Read in first varibale sent from Pi into Command variable
		Command = char(Wire.read());
		// Read in second variable sent from Pi into PWM_val variable
	  	PWM_val = int(Wire.read());
		// Make sure the PWM_val does not overflow or underflow
	  	if(PWM_val > 255){
			PWM_val = 255;
		}
		else if(PWM_val < 0){
			PWM_val =0;
		}
		// Call update function
		update();
	}  
}

// Function to send data back to Raspberry Pi if needed
void sendData(){
  Wire.write(Previous_Command);
}

// Function that updates wheels/excavator when command changes
void update(){
	// Chunk of code to control forward motion
	if(Command =='f'){
    digitalWrite(DIS,LOW);
    delayMicroseconds(20);
    digitalWrite(REV,LOW);
    analogWrite(FOR,PWM_val);
  }
  // Chuck of code to control reverse motion
  else if(Command =='r'){
    digitalWrite(DIS,LOW);
    delayMicroseconds(20);
    digitalWrite(FOR,LOW);
    analogWrite(REV, PWM_val);
  }
  // Chunk of code to completely disable wheel
  else if(Command ==' '){
    digitalWrite(DIS,HIGH);
    digitalWrite(REV,LOW);
    digitalWrite(FOR,LOW);
  }
  // Sets current command to previous command
  Previous_Command = Command;
}

