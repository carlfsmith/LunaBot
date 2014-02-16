
#ifndef UM6_H
#define	UM6_H

#include <stdint.h>
#include "register_masks.h"

class um6 {
public:
    um6(uint8_t rx, uint8_t tx);
    um6(const um6& orig);
    virtual ~um6();
    read(uint8_t regAddress);
    read(bool isBatch, uint8_t batchLength,uint8_t regAddress);
    write(uint8_t regAddress);
private:
    uint8_t packetParser(uint8_t* rxData, uint8_t rxLength, serialPacket* rx);
    
    uint8_t rxPin, txPin;
    
    
    float latitude, longitude, speed,
    n_position, e_position;
    
    typedef struct serialPacket {
        uint8_t address;
        uint8_t pt;
        uint16_t checkSum;
        uint8_t dataLength;
        uint8_t data[30];
    };    
    serialPacket rxPacket, txPacket;
    
};

#endif	/* UM6_H */