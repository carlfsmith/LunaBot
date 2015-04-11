#!/usr/bin/env python
import rospy
import math
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64

# Author: Carl Smith
# This ROS Node converts Joystick inputs from the joy node
# into commands for turtlesim

# Receives joystick messages (subscribed to Joy topic)
# then converts the joysick inputs into Twist commands
# axis 1 aka left stick vertical controls linear speed
# axis 0 aka left stick horizonal controls angular speed
def callback_joy(data):
	global desiredWheelRotSpeedL
	global desiredWheelRotSpeedR
	acc = data.axes[1]
	turn = data.axes[0]
	print wheelRotSpeedDx
	if turn > 0:	#if pressing left
		if desiredWheelRotSpeedL != -desiredWheelRotSpeedR:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*acc
		else:
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*acc
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*acc
	elif turn < 0:	#if pressing right
		if desiredWheelRotSpeedR != -desiredWheelRotSpeedL:
			desiredWheelRotSpeedR = desiredWheelRotSpeedR - wheelRotSpeedDx*acc
		else:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*acc
			desiredWheelRotSpeedR = desiredWheelRotSpeedR - wheelRotSpeedDx*acc
	else:		#if pressing forward or back
		if desiredWheelRotSpeedL < desiredWheelRotSpeedR:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*acc
		elif desiredWheelRotSpeedR < desiredWheelRotSpeedL:
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*acc
		else:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*acc
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*acc
	UL_wheel.publish(desiredWheelRotSpeedL)
	UR_wheel.publish(desiredWheelRotSpeedR)
	BL_wheel.publish(desiredWheelRotSpeedL)
	BR_wheel.publish(desiredWheelRotSpeedR)

def callback_UL(data):
	global v_UL
	v_UL = data.velocity[0]
	#print "in UL", v_UL

def callback_UR(data):
	global v_UR
	v_UR = data.velocity[0]
	#print "in UR", v_UR

def callback_BL(data):
	global v_BL
	v_BL = data.velocity[0]
	#print "in BL", v_BL

def callback_BR(data):
	global v_BR
	v_BR = data.velocity[0]
	#print "in BR", v_BR

def start():
	global v_UL
	global v_UR
	global v_BL
	global v_BR
	global UL_wheel
	global UR_wheel
	global BL_wheel
	global BR_wheel
	global desiredWheelRotSpeedL
	global desiredWheelRotSpeedR 
	global wheelRotSpeedDx
	v_UL = 0.0
	v_UR = 0.0
	v_BL = 0.0
	v_BR = 0.0
	desiredWheelRotSpeedL = 0.0
	desiredWheelRotSpeedR = 0.0
	wheelRotSpeedDx = 35*math.pi/180
	maxRotSpeed = 80
	UL_wheel = rospy.Publisher('lunabot/UL_wheel', Float64, queue_size=1)
	UR_wheel = rospy.Publisher('lunabot/UR_wheel', Float64, queue_size=1)
	BL_wheel = rospy.Publisher('lunabot/BL_wheel', Float64, queue_size=1)
	BR_wheel = rospy.Publisher('lunabot/BR_wheel', Float64, queue_size=1)
	rospy.init_node('controller')
	rospy.Subscriber("joy", Joy, callback_joy)
	rospy.Subscriber("/vrep/ULMotorData", JointState, callback_UL)
	rospy.Subscriber("/vrep/URMotorData", JointState, callback_UR)
	rospy.Subscriber("/vrep/BLMotorData", JointState, callback_BL)
	rospy.Subscriber("/vrep/BRMotorData", JointState, callback_BR)
	while not rospy.is_shutdown():
		#slow down ronot to a stop if no control input is detected
		if desiredWheelRotSpeedL > desiredWheelRotSpeedR and desiredWheelRotSpeedR > wheelRotSpeedDx:
			#forward right
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*2
			#print("1\n")
		elif desiredWheelRotSpeedL > desiredWheelRotSpeedR and desiredWheelRotSpeedL < wheelRotSpeedDx*-1:
			#reversing left
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*2
			#print("2\n")
		elif desiredWheelRotSpeedL < desiredWheelRotSpeedR and desiredWheelRotSpeedL > wheelRotSpeedDx:
			#turning left
			desiredWheelRotSpeedR = desiredWheelRotSpeedR - wheelRotSpeedDx*2
			#print("3\n")
		elif desiredWheelRotSpeedL < desiredWheelRotSpeedR and desiredWheelRotSpeedR < wheelRotSpeedDx*-1:
			#reverse right
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*2
			#print("4\n")
		elif desiredWheelRotSpeedL > wheelRotSpeedDx*2 and desiredWheelRotSpeedR > wheelRotSpeedDx:
			#using wheelRotSpeedDx because of the deceleration speed: wheelRotSpeedDx*2
			#going forward
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*2
			desiredWheelRotSpeedR = desiredWheelRotSpeedR - wheelRotSpeedDx*2
			#print("5\n")
		elif desiredWheelRotSpeedL < wheelRotSpeedDx*-2 and desiredWheelRotSpeedR < wheelRotSpeedDx*-1:
			#going in reverse
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*2
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*2
			#print("6\n")
		elif desiredWheelRotSpeedL > 0.000001 and desiredWheelRotSpeedR > 0.000001:
			#same as previous two; handles floating point anomaly
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx
			desiredWheelRotSpeedR = desiredWheelRotSpeedR - wheelRotSpeedDx
			#print("7\n" .. desiredWheelRotSpeedL .. "," .. desiredWheelRotSpeedR .. "\n")
		elif desiredWheelRotSpeedL < -0.000001 and desiredWheelRotSpeedR < -0.000001:
			#the reverse of the previous else
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx
			#print("8\n")
		else:
			#settle to stationary
			desiredWheelRotSpeedL = 0
			desiredWheelRotSpeedR = 0
			#print("9\n")
	rospy.spin()

if __name__ == '__main__':
	start()
