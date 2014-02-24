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
reg GPS_alt = 0x79;
reg GPS_n_pos = 0x7A;
reg GPS_e_pos = 0x7B;
reg GPS_alt_pos = 0x7C;
reg GPS_speed = 0x7D;
reg GPS_sat_sum = 0x7E;
reg GPS_sat_1n2 = 0x7F;
reg GPS_sat_3n4 = 0x80;
reg GPS_sat_5n6 = 0x81;
reg GPS_sat_7n8 = 0x82;
reg GPS_sat_9n10 = 0x83;
reg GPS_sat_11n12 = 0x84;

//Data Registers
reg status = 0x55;
reg xy_gyro_raw = 0x56;
reg xy_gyro_proc = 0x5C;
reg z_gyro_raw = 0x57;
reg z_gyro_proc = 0x5D;
reg xy_accel_raw = 0x58;
reg xy_accel_proc = 0x5E;
reg z_accel_raw = 0x59;
reg z_accel_proc = 0x5F;
reg xy_mag_raw = 0x5A;
reg xy_mag_proc = 0x60;
reg z_mag_raw = 0x5B;
reg z_mag_proc = 0x61;

//Configuration Registers
reg communication = 0x00;
reg misc_config = 0x01;
    
//Command Registers
#endif	/* REGISTER_MASKS_H */

