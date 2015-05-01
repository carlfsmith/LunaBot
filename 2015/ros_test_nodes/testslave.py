#!/usr/bin/env python
import smbus
import time

# Bus connecting Raspberry Pi and Arduino
bus = smbus.SMBus(1)

# Address of Arduino Slave
address = 0x04

# Function for writing to the Arduino, sends the direction and speed 
def writeNumber(command, value):
    bus.write_byte_data(address, command, value)
    return -1
    
# Function for requesting data back from Arduino, at the moment returns
# the direction command sent to it, needs to be changed to receive an
# array from the arduino for multiple info (temp, speed, etc)
def readNumber():
    number = bus.read_byte(address)
    return number
    
while True:
    # com and var variables that take keyboard input 
    com = input("Direction Command Here: ")
    var = input("Speed Here: ")
    # Testing to make sure if either com or var are char/string it's 
    # converted to ASCII code
    if type(com) is str: com = ord(com)
    if type(var) is str: var = ord(var)
    # Testing to make sure the user actually gave input
    if not com:
        continue
    if not var:
        continue
        
    # Call to function to write the input from above to Arduino
    writeNumber(com, var)
    print "Raspberry Pi: ", com, " & ", var, " sent to Arduino."
    # Delay statement
    time.sleep(1)
    
    # Call to function to request data from Arduino and save in number variable
    number = readNumber()
    print "Arduino: ", number1, " sent to Raspberry Pi."
    print
