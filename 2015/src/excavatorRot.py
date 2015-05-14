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
address = 0x07

# Function for writing to the Arduino, sends the direction and speed 
def writeNumber(command, value):
    bus.write_byte_data(address, command, value)
    return -1
        
# Function for requesting data back from Arduino
def readNumber(address):
    number = bus.read_byte(address)
    return number

def rotate(data):
    #excavate=rotate excavator
    global excavate
    #data.buttons[10] contains a button info (1=pressed, 0=not pressed)
    excavate = data.buttons[10]
    #global flag
    speed = 0
    seconds = rospy.get_time()
    #excavate forward
    while (rospy.get_time() - seconds) < 5:
        #increment speed
        if speed < 250:
            speed = speed + 10
        elif speed == 250:
            speed = 255
        writeNumber(102, speed)
        rate.sleep()
        print "excavator rotating faster"
    while speed > 0:
        if speed > 5:
            speed = speed -10
        elif speed == 5:
            speed = 0
        writeNumber(32, speed)
	rate.sleep()
	rate.sleep()
    seconds = rospy.get_time()
    #excavate backward
    while (rospy.get_time() - seconds) < 5:
        #increment speed
        if speed < 250:
            speed = speed + 10
        elif speed == 250:
            speed = 255
        writeNumber(114, speed)
        rate.sleep()
        print "excavator rotating slower"
    while speed > 0:
        if speed > 5:
            speed = speed -10
        elif speed == 5:
            speed = 0
        writeNumber(32, speed)
	rate.sleep()
	rate.sleep()
    excavate = 0
 
def run():
    rospy.init_node('excavatorRot')
    global excavate #tracks excavate button (push of R joystick)
    global rate #set to one second
    excavate = 0
    rate = rospy.Rate(10) #10hz
    rospy.Subscriber("joy", Joy, rotate, queue_size=1)
    rospy.spin()

if __name__ == '__main__':
    try:
        run()
    except rospy.ROSInterruptException:
        pass
