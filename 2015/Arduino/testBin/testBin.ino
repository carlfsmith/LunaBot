#include <Wire.h>
#define SLAVE_ADDRESS Ox08 // just different than the other

// Bin pins: up, down, disable
int UP = 5;
int DOWN = 3;
int DIS = 4;

volatile char Command = '0';
char Previous_Command = '0';

void setup(){
  pinMode(UP,OUTPUT);
  pinMode(DOWN,OUTPUT);
  pinMode(DIS,OUTPUT);
  digitalWrite(DIS,HIGH);
  Wire.begin(SLAVE_ADDRESS);
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);
}

void loop(){
  delay(100);
}

void receiveData(int byteCount){
  while(Wire.available())
    Command = char(Wire.read());
  if(Command != Previous_Command)
    update();
}

void sendData(){
  Wire.write(Previous_Command);
}

void update(){
  // Moves bin up
  if(Command == 'u'){
    digitalWrite(DIS,LOW);
    delay (100);
    digitalWrite(DOWN,LOW);
    digitalWrite(UP,HIGH);
  }
  // Moves bin down
  else if(Command == 'd'){
    digitalWrite(DISB,LOW);
    delay (100);
    digitalWrite(DOWNB,HIGH);
    digitalWrite(UPB,LOW);
  }
  // Disables bin
  else if(Command == ' '){
    digitalWrite(DISB, HIGH);
    digitalWrite(DOWNB, LOW);
    digitalWrite(UPB, LOW);  
  }
  Previous_Command = Command;
}
  
