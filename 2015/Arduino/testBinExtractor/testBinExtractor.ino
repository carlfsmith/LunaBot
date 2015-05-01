#include <Wire.h>
#define SLAVE_ADDRESS 0x04 // will change

// Bin pins: up, down, disable
int UPB = 5;
int DOWNB = 3;
int DISB = 4;
// Would need to know where these are pinned
// Extractor pins: disable, up, down, on, off
int DISE;
int UPE;
int DOWNE;
int ON;
int OFF;

// Command has two options, 'b' for Bin, 'e' for Extractor
volatile char Command = '0';
char Previous_Command = '0';
// Power for up, down, on, off, disable 
volatile char Power = '0';
char Previous_Power = '0';

void setup() {
  pinMode(UPB,OUTPUT);
  pinMode(DOWNB,OUTPUT);
  pinMode(DISB,OUTPUT);
  pinMode(DISE,OUTPUT);
  pinMode(UPE,OUTPUT);
  pinMode(DOWNE,OUTPUT);
  pinMode(ON,OUTPUT);
  pinMode(OFF,OUTPUT);
  digitalWrite(DISB,HIGH);
  digitalWrite(DISE,HIGH);
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
  //Wire.write(PWM_val);
}

void update(){
  // Selection of code for Bin
  if(Command =='b'){
    // Moves bin up
    if(Power == 'u'){
      digitalWrite(DISB,LOW);
      delay (100);
      digitalWrite(DOWNB,LOW);
      digitalWrite(UPB,HIGH);
    }
    // Moves bin down
    else if(Power == 'd'){
      digitalWrite(DISB,LOW);
      delay (100);
      digitalWrite(DOWNB,HIGH);
      digitalWrite(UPB,LOW);
    }
    // Disables bin
    else if(Power == ' '){
      digitalWrite(DISB, HIGH);
      digitalWrite(DOWNB, LOW);
      digitalWrite(UPB, LOW);  
    }
  // Selection of code for Extractor  
  if(Command =='e'){
    // Moving Extractor up
    if(Power == 'u'){ 
      digitalWrite(DISE,LOW);
      delay (100);
      digitalWrite(DOWNE,LOW);
      digitalWrite(UPE,HIGH);
      digitalWrite(ON,LOW);
      digitalWrite(OFF,LOW);
    }
    // Moving Extractor down
    else if(Power == 'd'){
      digitalWrite(DISE,LOW);
      delay (100);
      digitalWrite(DOWNE,HIGH);
      digitalWrite(UPE,LOW);
      digitalWrite(ON,LOW);
      digitalWrite(OFF,LOW);
    }
    // Turning Extractor on
    else if(Power == 'o'){
      digitalWrite(DISE,LOW);
      delay(100);
      digitalWrite(DOWNE,LOW);
      digitalWrite(UPE,LOW);
      digitalWrite(ON,HIGH);
      digitalWrite(OFF,LOW);
    }
    // Turning Extractor off
    else if(Power == 'f'){
      digitalWrite(DISE,LOW);
      delay(100);
      digitalWrite(DOWNE,LOW);
      digitalWrite(UPE,LOW);
      digitalWrite(ON,LOW);
      digitalWrite(OFF,HIGH);
    }
    // Disable Extractor 
    else if(Power == ' '){
      digitalWrite(DISE,HIGH);
      delay(100);
      digitalWrite(DOWNE,LOW);
      digitalWrite(UPE,LOW);
      digitalWrite(ON,LOW);
      digitalWrite(OFF,LOW);
    }
  }
  Previous_Command = Command;
  Previous_Power = Power;
}
