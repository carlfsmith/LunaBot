#include <Wire.h>
#define SLAVE_ADDRESS 0x04 // know this will change

// Extractor pins: up, down, on, off, disable 
// Need pin location
int UP;
int DOWN;
int ON;
int OFF;
int DIS;

// Command for up or down
volatile char Command = '0';
char Previous_Command = '0';
// Power for on or off
volatile char Power = '0';
char Previous_Power = '0';

void setup(){
  pinMode(UP,OUTPUT);
  pinMode(DOWN,OUTPUT);
  pinMode(ON,OUTPUT);
  pinMode(OFF,OUTPUT);
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
  while(Wire.available()){
    Command = char(Wire.read());
    Power = char(Wire.read());
  }  
  if(Command != Previous_Command || Power != Previous_Power)
    update();  
}

void sendData(){
  Wire.write(Previous_Command);
}

void update(){
  // Move extractor down
  if(Command == 'd'){
    digitalWrite(DISE,LOW);
    delay (100);
    digitalWrite(DOWNE,HIGH);
    digitalWrite(UPE,LOW);
    digitalWrite(ON,LOW);
    digitalWrite(OFF,LOW);
    // Turn extractor on
    if(Power == 'o')
      digitalWrite(ON,HIGH);
    // Turn extractor off
    else if(Power == 'f')
      digitalWrite(OFF,HIGH);
  }
  // Move extractor up
  else if(Command == 'u'){
    digitalWrite(DISE,LOW);
    delay (100);
    digitalWrite(DOWNE,LOW);
    digitalWrite(UPE,HIGH);
    digitalWrite(ON,LOW);
    digitalWrite(OFF,LOW);
    // Turn extractor on
    if(Power == 'o')
      digitalWrite(ON,HIGH);
    // Turn extractor off
    else if(Power == 'f')
      digitalWrite(OFF,HIGH);
  }
  else if(Command == ' '){
    digitalWrite(DIS,HIGH);
    delay(100);
    digitalWrite(DOWN,LOW);
    digitalWrite(UP,LOW);
    digitalWrite(ON, LOW);
    digitalWrite(OFF,LOW);
  }
  Previous_Command = Command;
  Previous_Power = Power;
}
