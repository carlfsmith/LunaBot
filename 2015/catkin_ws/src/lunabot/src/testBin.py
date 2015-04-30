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

#import smbus
import time
import rospy
from std_msgs.msg import String
from sensor_msgs.msg import Joy

#Bus connecting to Raspberry Pi and Arduino
#bus = smbus.SMBus(1)
#Address of Arudino Slave
#address = 0x04

# Function for writing to the Arduino, sends the direction and speed 
#def writeNumber(command, value):
#    bus.write_byte_data(address, command, value)
#    return -1
        
# Function for requesting data back from Arduino
#def readNumber():
#    number = bus.read_byte(address)
#    return number

def callback(data):
    #data.axes containes dpad up button info 1 for pressed, 
    #0 for not pressed
    move = data.buttons[1]
    speed = 255
    #ascii u=117 (for up) and ascii d=100 (for down)
    if move == 1: 
        seconds = rospy.get_time()
	#lower bin
        while (rospy.get_time() -seconds) < 5:
            #writeNumber(102, speed)
            time.sleep(0.01)
            #received = str(readNumber())
            received = str("lower bin")
            rospy.loginfo(received)
            pub.publish(received)
        seconds = rospy.get_time()
        #pause
	while (rospy.get_time() -seconds) < 2:
	    #writeNumber(32, speed)
	    time.sleep(0.01)
	    #received = str(readNumber())
            received = str("pause")
	    rospy.loginfo(received)
            pub.publish(received)
	seconds = rospy.get_time()
	#raise bin
	while (rospy.get_time() -seconds) < 5:
            #writeNumber(114, speed)
            time.sleep(0.01)
            #received = str(readNumber())
            received = str("raise bin")
            rospy.loginfo(received)
            pub.publish(received)
	seconds = rospy.get_time()
	#pause
	while (rospy.get_time() -seconds) < 2:
	    #writeNumber(32, speed)
	    time.sleep(0.01)
	    #received = str(readNumber())
	    received = str("pause")
	    rospy.loginfo(received)
	    pub.publish(received)
    move = 0
    time.sleep(2)

def run():
    global pub
    rospy.Subscriber("joy", Joy, callback)
    while(rospy.is_shutdown() == False):
	time.sleep(2)
    	pub = rospy.Publisher('chatter', String, queue_size=10)
    	rospy.init_node('talker', anonymous=True)
    	rospy.spin()

if __name__ == '__main__':
    try:
        run()
    except rospy.ROSInterruptException:
        pass
