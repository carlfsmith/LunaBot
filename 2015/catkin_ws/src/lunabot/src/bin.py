#!/usr/bin/env python
# Author: Rachel Findley
# Code used for testing one motor to move forwards, backwards, and stop
# Software License Agreement (BSD License)
#
# Copyright (c) 2008, Willow Garage, Inc.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
#  * Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
#  * Redistributions in binary form must reproduce the above
#    copyright notice, this list of conditions and the following
#    disclaimer in the documentation and/or other materials provided
#    with the distribution.
#  * Neither the name of Willow Garage, Inc. nor the names of its
#    contributors may be used to endorse or promote products derived
#    from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
# FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
# COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
# BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
# LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
# ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# Revision $Id$

import smbus
import time
import rospy
from std_msgs.msg import String
from sensor_msgs.msg import Joy

#Bus connecting to Raspberry Pi and Arduino
bus = smbus.SMBus(1)
#Address of Arudino Slave
address = 0x04

# Function for writing to the Arduino, sends the direction and speed 
def writeNumber(command, value):
    bus.write_byte_data(address, command, value)
    return -1
        
# Function for requesting data back from Arduino
def readNumber():
    number = bus.read_byte(address)
    return number

def callback(data):
    #data.buttons[1] contains button B info (1=pressed, 0=not pressed)
    global move
    global speed
    move = data.buttons[1]
    #ascii u=117 (for up) and ascii d=100 (for down)
    if move == 1: 
	#move bin up
	seconds = rospy.get_time()
        while (rospy.get_time() -seconds) < 5:
            #increment speed
            if speed < 250:
                speed = speed + 10
            elif speed == 250:
                speed = speed + 5
            #write command to arduino
            writeNumber(117, speed)
            #sleep for one second
            rate.sleep()
            #print for testing purposes
            print "speed: " + str(speed)
            print "going up..."
	#slow motor to halt
        while speed > 0:
            #decrement motor
	    if speed > 5:
                speed = speed - 10
            elif speed == 5:
                speed = 0
            #write command to arduino
            writeNumber(32, speed)
	    #sleep for 2 seconds
            rate.sleep()
            rate.sleep()
            #print for testing
	    print "speed: " + str(speed)
            print "slowing down..."
	#move bin down
	seconds = rospy.get_time()
	while (rospy.get_time() -seconds) < 5:
            #increment speed
            if speed < 250:
                speed = speed + 10
            elif speed == 250:
                speed = speed + 5
            #write command to arduino
            writeNumber(100, speed)
	    #sleep for one second
            rate.sleep()
            #print for testing purposes
	    print "speed: " + str(speed)
            print "going down..."
	#slow motor to halt
	while speed > 0:
            #decrement speed
	    if speed > 5:
                speed = speed - 10
            elif speed == 5:
                speed = 0
            #write command to arduino
            writeNumber(32, speed)
	    #sleep for 2 seconds
            rate.sleep()
            rate.sleep()
            #pring for testing purposes
	    print "speed: " + str(speed)
            print "slowing down..."
    #reset move
    move = 0

def run():
    #initialize node
    rospy.init_node('bin')
    #global variables
    global move #tracks pressed B button
    global speed #tracks varying speed
    global rate #set for one second
    move = 0
    speed = 0
    rospy.Subscriber("joy", Joy, callback, queue_size=1)
    rate = rospy.Rate(10) #10hz
    rospy.spin()

if __name__ == '__main__':
    try:
        run()
    except rospy.ROSInterruptException:
        pass
