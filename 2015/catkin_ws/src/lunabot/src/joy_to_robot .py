#!/usr/bin/env python
import rospy
import math
from std_msgs.msg import Float64
from sensor_msgs.msg import JointState
import sys
import smbus

# Author: Carl Smith

# Bus connecting Raspberry Pi and Arduino
bus = smbus.SMBus(1)
# Address of Arduino Slave
address_UL = 0x04
address_UR = 0x08
address_BL = 0x0C
address_BR = 0x10

#used to throw pwm exception
class MyException(Exception):
	pass

#function for determining sign, return 'r' for negative and 'f' for forward
def sign(val):
	if val < 0:
		return "r"
	else:
		return "f"

#function for writing to the Arduino, sends the direction and speed 
def convert(address, pwm):
    	bus.write_byte_data(address, sign(pwm), abs(int(pwm)))

#controller sub callback functions
def callback_UL(pkg):
	pwm = pkg.data
	if abs(pwm) < 0 or abs(pwm) > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(address_UL, pwm)

def callback_UR(pkg):
	pwm = pkg.data
	if abs(pwm) < 0 or abs(pwm) > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(address_UR, pwm)

def callback_BL(pkg):
	pwm = pkg.data
	if abs(pwm) < 0 or abs(pwm) > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(address_BL, pwm)

def callback_BR(pkg):
	pwm = pkg.data
	if abs(pwm) < 0 or abs(pwm) > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(address_BR, pwm)

#main-------------------------------------------------------------
if __name__ == '__main__':
	rospy.init_node('controller_converter')
	#subscribe to controller topics
	rospy.Subscriber("/lunabot/UL_wheel", Float64, callback_UL)
	rospy.Subscriber("/lunabot/UR_wheel", Float64, callback_UR)
	rospy.Subscriber("/lunabot/BL_wheel", Float64, callback_BL)
	rospy.Subscriber("/lunabot/BR_wheel", Float64, callback_BR)
	rospy.spin()
