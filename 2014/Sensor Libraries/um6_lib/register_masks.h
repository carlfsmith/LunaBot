/* 
 * File:   register_masks.h
 * Author: CE2112
 *
 * Created on February 16, 2014, 3:32 AM
 */

#ifndef REGISTER_MASKS_H
#define	REGISTER_MASKS_H

#include <stdint.h>


typedef uint8_t reg;

//External GPS Registers
//Only used when um6 is connected to external GPS device
reg GPS_long = 0x77;
reg GPS_lat = 0x78;
reg GPS_n_pos = 0x7A;
reg GPS_e_pos = 0x7B;
reg GPS_speed = 0x7D;

//Data Registers


//Configuration Registers
reg communication = 0x00;
    
//Command Registers
#endif	/* REGISTER_MASKS_H */

