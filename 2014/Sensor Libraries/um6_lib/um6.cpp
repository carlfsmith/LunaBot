 /* 
 * File:   um6.cpp
 * Author: CE2112
 * 
 * Created on February 12, 2014, 2:14 PM
 */

#include "um6.h"

um6::um6(uint8_t rx, uint8_t tx) {
    rxPin = rx;
    txPin = tx;
}

um6::um6(const um6& orig) {
}

um6::~um6() {
}

um6::read(uint8_t regAddress) {
    
}

um6::read(bool isBatch, uint8_t batchLength, uint8_t regAddress) {
    
}

um6::write(uint8_t regAddress) {
    
}

um6::packetParser(uint8_t* rxData, uint8_t rxLength, serialPacket* rx){   
    if(rxLength < 7)
        return 1;
    
    uint8_t index, packetIndex = 0;
    
    for(index = 0; index < rxLength-2; index++) {
        if(rxData[index] == 's' && rxData[index+1] == 'n' && rxData[index+2] == 'p') {
            packetIndex = index;
            break;
        }            
    }
    
    if(packetIndex == rxLength-2)
        return 2;
    
    if(rxLength-packetIndex < 7)
        return 3;
    
    rx->pt = rxData[packetIndex+3];
    
    uint8_t hasData = (rx->pt >> 7) & 0x01;
    uint8_t isBatch = (rx->pt >> 6) & 0x01;
    uint8_t batchLength = (rx->pt >> 2) & 0x0F;
    
    if(hasData) {
        if(isBatch)
            rx->dataLength = 4*batchLength;
        
        else
            rx->dataLength = 4;
    }
    
    else
        rx->dataLength = 0;
    
    if(rxLength-packetIndex < rx->dataLength+5)
        return 3;
    
    rx->address = rxData[packetIndex + 4];
    
    uint16_t computedChecksum = 's'+'n'+'p'+rx->pt+rx->address;
    
    for(index = 0; index < rx->dataLength; index++) {
        rx->data[index] = rxData[index+packetIndex+5];
        computedChecksum += rx->data[index];
    }
    
    uint16_t receivedChecksum = (rxData[packetIndex+rx->dataLength+5] << 8);
    receivedChecksum |= rxData[packetIndex+rx->dataLength+6];
    
    if(receivedChecksum != computedChecksum)
        return 4;
    
    return 0;
}
