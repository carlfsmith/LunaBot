/* 
 * File:   lm34.h
 * Author: CE2112
 *
 * Created on February 16, 2014, 1:51 PM
 */

#ifndef LM34_H
#define	LM34_H
#include <C:\Arduino\hardware\arduino\cores\arduino\Arduino.h>
#include <stdint.h>

class lm34 {
public:
    lm34(uint8_t analogPin);
    lm34(uint8_t analogPin, float stepVoltage);
    void read();
    void changeVoltageStep(float newStep);
    void changeScale(char scale);
    lm34(const lm34& orig);
    virtual ~lm34();
private:
    uint8_t pin;
    float conversation;    
    float Temp;
    char tempScale = 'F';
    float getTemp();

};

#endif	/* LM34_H */

