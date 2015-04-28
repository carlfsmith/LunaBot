#!/usr/bin/env python
import rospy
import math
from std_msgs.msg import Float64
from sensor_msgs.msg import JointState
import sys

# Author: Carl Smith

#create publishers
UL_wheel = rospy.Publisher('lunabot/to_vrep/UL_wheel', Float64, queue_size=1)
UR_wheel = rospy.Publisher('lunabot/to_vrep/UR_wheel', Float64, queue_size=1)
BL_wheel = rospy.Publisher('lunabot/to_vrep/BL_wheel', Float64, queue_size=1)
BR_wheel = rospy.Publisher('lunabot/to_vrep/BR_wheel', Float64, queue_size=1)

class MyException(Exception):
	pass

def convert(pwm):
	vel = (pwm - 127)/25
	return vel

#controller sub callback functions
def callback_UL(pkg):
	pwm = pkg.data
	if pwm < 0 or pwm > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(pwm)
		UL_wheel.publish(vel)

def callback_UR(pkg):
	pwm = pkg.data
	if pwm < 0 or pwm > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(pwm)
		UR_wheel.publish(vel)

def callback_BL(pkg):
	pwm = pkg.data
	if pwm < 0 or pwm > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(pwm)
		BL_wheel.publish(vel)

def callback_BR(pkg):
	pwm = pkg.data
	if pwm < 0 or pwm > 255:
		raise MyException("cannot call using a pwm out of range 0-255")
	else:
		vel = convert(pwm)
		BR_wheel.publish(vel)

#main-------------------------------------------------------------
if __name__ == '__main__':
	rospy.init_node('controller_converter')
	#subscribe to controller topics
	rospy.Subscriber("/lunabot/UL_wheel", Float64, callback_UL)
	rospy.Subscriber("/lunabot/UR_wheel", Float64, callback_UR)
	rospy.Subscriber("/lunabot/BL_wheel", Float64, callback_BL)
	rospy.Subscriber("/lunabot/BR_wheel", Float64, callback_BR)
	rospy.spin()
