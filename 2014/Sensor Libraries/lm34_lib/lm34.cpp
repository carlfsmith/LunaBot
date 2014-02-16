/* 
 * File:   lm34.cpp
 * Author: CE2112
 * 
 * Created on February 16, 2014, 1:51 PM
 */

#include "lm34.h"

lm34::lm34(uint8_t analogPin) {
    changeVoltageStep(4.9);
    pin = analogPin;
}

 lm34::lm34(uint8_t analogPin, float stepVoltage) {
     changeVoltageStep(stepVoltage);
     pin = analogPin;
 }
 
lm34::lm34(const lm34& orig) {

}

lm34::~lm34() {

}

void lm34::read() {
    Serial.println(getTemp() + " " + tempScale);
}

void lm34::changeScale(char scale) {
    if(scale == 'f' || scale == 'F' || scale == 'c' || scale == 'C' || scale == 'k' || scale == 'K') {
        tempScale = toUpperCase(scale);
    }
    
    return;
}

void lm34::changeVoltageStep(float newStep) {
    conversation = newStep/10.0; //sensor report in degrees F by default.    
}

float lm34::getTemp() {
    Temp = analogRead(pin)/conversation;
    
    switch (tempScale) {
        case 'C': 
            Temp = (Temp-32)*(5.0/9.0);
            break;
        case 'K': 
            Temp = (Temp-32)*(5.0/9.0)-273.15;
            break;
        default:
            return Temp;
    }
    
    return Temp;                           
}