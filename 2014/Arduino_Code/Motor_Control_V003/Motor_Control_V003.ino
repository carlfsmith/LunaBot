
#include <ctype.h>

volatile char Command = '0';
char Previous_Command = '0';
int Disable = 1;
int OSMC2_Forward_Pin = 7, OSMC2_Reverse_Pin = 6; //left side
int OSMC1_Forward_Pin = 12, OSMC1_Reverse_Pin = 13; //right side
int OSMC3_Forward_Pin = 28, OSMC3_Reverse_Pin = 26;
int OSMC2_Disable = 5, OSMC1_Disable = 11, OSMC3_Disable = 30;
int dreg1 = 52, dreg2 =50;
int Target_Time = 3906;
int PWM_Value = 50, Shoot_Thru_Delay = 80;
int Dig_PWM_Value =150;
int Gear = 1;
int LED = 3;
int bActuatorP = 53;
int bActuatorD = 51;

float iOSMC1 = 0, iOSMC2 = 0;
int osmc1Current = 7, osmc2Current = 9;

void setup() {
  pinMode(OSMC1_Disable, OUTPUT);
  pinMode(OSMC2_Disable, OUTPUT);
  pinMode(OSMC3_Disable, OUTPUT);
  digitalWrite(OSMC1_Disable, HIGH);
  digitalWrite(OSMC2_Disable, HIGH);
  digitalWrite(OSMC3_Disable, HIGH);
  pinMode(OSMC1_Forward_Pin, OUTPUT);
  pinMode(OSMC2_Forward_Pin, OUTPUT);
  pinMode(OSMC1_Reverse_Pin, OUTPUT);
  pinMode(OSMC2_Reverse_Pin, OUTPUT);
  pinMode(OSMC3_Reverse_Pin, OUTPUT);
  pinMode(OSMC3_Forward_Pin, OUTPUT);
  pinMode(bActuatorP, OUTPUT);
  pinMode(bActuatorD, OUTPUT);
  pinMode(osmc1Current, INPUT);
  pinMode(osmc2Current, INPUT);
  pinMode(LED, OUTPUT);
  pinMode(dreg1, OUTPUT);
  pinMode(dreg2, OUTPUT);

  Serial.begin(9600);
}

void loop() {
  if(Command == 'c' || Command == 'x' || Command == 'z')
    moveBin();
    
  if(Command == 'k' || Command == 'l'|| Command == 'j'|| Command == 'm')
    dig();
    
  if(Command != Previous_Command)
    update();
  
  powerMeasure();
}


void update() {
  if(Command == 'w' || Command == 'a' || Command == 's' || Command == 'd') {
    digitalWrite(OSMC1_Disable, HIGH);
    digitalWrite(OSMC2_Disable, HIGH);
    analogWrite(OSMC1_Forward_Pin, 0);
    analogWrite(OSMC2_Forward_Pin, 0);
    analogWrite(OSMC1_Reverse_Pin, 0);
    analogWrite(OSMC2_Reverse_Pin, 0);
  }
    
    if(Command == '1' || Command == '2'|| Command == '3') {
      if(Command == '1')
        Gear = 1;
      else if(Command == '2')
        Gear = 2;
       else if(Command == '3')
        Gear = 3;
      Command = Previous_Command;  
    }
    
    if(Command == 'w') {
        digitalWrite(OSMC1_Disable, LOW);
        digitalWrite(OSMC2_Disable, LOW);
        delay(Shoot_Thru_Delay);
        analogWrite(OSMC1_Forward_Pin, (PWM_Value*Gear));
        analogWrite(OSMC2_Forward_Pin, (PWM_Value*Gear));
        //test(2);
    }
    else if(Command == 's') {
        digitalWrite(OSMC1_Disable, LOW);
        digitalWrite(OSMC2_Disable, LOW);
        delay(Shoot_Thru_Delay);
        analogWrite(OSMC1_Reverse_Pin, (PWM_Value*Gear));
        analogWrite(OSMC2_Reverse_Pin, (PWM_Value*Gear));
        //test(3);
    }
    else if(Command == 'a') {
        digitalWrite(OSMC1_Disable, LOW);
        digitalWrite(OSMC2_Disable, LOW);
        delay(Shoot_Thru_Delay);
        analogWrite(OSMC1_Forward_Pin, (PWM_Value*Gear));
        analogWrite(OSMC2_Reverse_Pin, (PWM_Value*Gear));
        //test(2);
    }
    else if(Command == 'd') {
        digitalWrite(OSMC1_Disable, LOW);
        digitalWrite(OSMC2_Disable, LOW);
        delay(Shoot_Thru_Delay);
        analogWrite(OSMC1_Reverse_Pin, (PWM_Value*Gear));
        analogWrite(OSMC2_Forward_Pin, (PWM_Value*Gear));
        //test(5);
    }
    else if(Command == 'p') {
      //Serial.print("4.31");
      Serial.println(iOSMC1 + iOSMC2);
      //test(1);
    }
    
    Previous_Command = Command;
}

void moveBin() {
  if(Command == 'z') {
    digitalWrite(bActuatorP, LOW);
    digitalWrite(bActuatorD, LOW);
  }
  
  if(Command == 'c') {
    digitalWrite(bActuatorP, HIGH);
    digitalWrite(bActuatorD, LOW);
  }
  
  if(Command == 'x') {
    digitalWrite(bActuatorP, HIGH);
    digitalWrite(bActuatorD, HIGH);
  } 
  
  Previous_Command = Command;
}

void dig() {
  if(Command == 'k') {
    digitalWrite(dreg1, LOW); 
  }
  
  if(Command == 'l') {
    digitalWrite(dreg1, HIGH);
    delay(800);
    digitalWrite(dreg1, LOW);   
  } 
  
  Previous_Command = Command;
}

void powerMeasure() {
   iOSMC1 += (analogRead(osmc1Current)/(25*51))/0.002;
   iOSMC2 += (analogRead(osmc2Current)/(25*51))/0.002;   
}

void serialEvent() {
    Command = (char)Serial.read(); 
    
  if(Command == '0' || Command == ' '){
    digitalWrite(OSMC1_Disable, HIGH);
    digitalWrite(OSMC2_Disable, HIGH);
    digitalWrite(OSMC3_Disable, HIGH);
    digitalWrite(bActuatorP, LOW);
    analogWrite(OSMC1_Forward_Pin, 0);
    analogWrite(OSMC2_Forward_Pin, 0);
    analogWrite(OSMC1_Reverse_Pin, 0);
    analogWrite(OSMC2_Reverse_Pin, 0);
    analogWrite(OSMC3_Forward_Pin, 0);
    analogWrite(OSMC3_Reverse_Pin, 0);
  }
    
  //Serial.println(Command); 
}

void test(int reps) {
  for(int i = 0; i < reps; i++) {
    digitalWrite(LED, HIGH);
    delay(1000);
    digitalWrite(LED, LOW);
    delay(1000);
  }
}
