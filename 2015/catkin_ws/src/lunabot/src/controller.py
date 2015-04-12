#!/usr/bin/env python
import rospy
import math
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64

# Author: Carl Smith

v_UL = 0.0
v_UR = 0.0
v_BL = 0.0
v_BR = 0.0
desiredWheelRotSpeedL = 0.0
desiredWheelRotSpeedR = 0.0
axis_x = 0.0
axis_y = 0.0
wheelRotSpeedDx = 35*math.pi/800

def callback_joy(data):
	global axis_x
	global axis_y
	axis_x = data.axes[0]
	axis_y = data.axes[1]
	
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

def move():
	global desiredWheelRotSpeedL
	global desiredWheelRotSpeedR
	velocity	#set by y axis
	turn_degree	#set by x axis, determines L/R wheel difference
	if axis_x > 0:	#if pressing left
		print 'going left'
		if desiredWheelRotSpeedL > -desiredWheelRotSpeedR:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*axis_x
		elif desiredWheelRotSpeedL < -desiredWheelRotSpeedR:
			desiredWheelRotSpeedL = -desiredWheelRotSpeedR
		else:
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*axis_x
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*axis_x
	elif axis_x < 0:	#if pressing right
		print 'going right'
		if desiredWheelRotSpeedR > -desiredWheelRotSpeedL:
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*axis_x
		elif desiredWheelRotSpeedR < -desiredWheelRotSpeedL:
			desiredWheelRotSpeedR = -desiredWheelRotSpeedL
		else:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL - wheelRotSpeedDx*axis_x
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*axis_x
	elif axis_y > 0 or axis_y < 0:		#if pressing forward or back
		print 'going forward or backward'
		if desiredWheelRotSpeedL < desiredWheelRotSpeedR:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*axis_y
		elif desiredWheelRotSpeedR < desiredWheelRotSpeedL:
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*axis_y
		else:
			desiredWheelRotSpeedL = desiredWheelRotSpeedL + wheelRotSpeedDx*axis_y
			desiredWheelRotSpeedR = desiredWheelRotSpeedR + wheelRotSpeedDx*axis_y
	else:
		print 'other...'
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

if __name__ == '__main__':
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
	rate = rospy.Rate(10) # 10hz
	while not rospy.is_shutdown():
		move()
		UL_wheel.publish(desiredWheelRotSpeedL)
		UR_wheel.publish(desiredWheelRotSpeedR)
		BL_wheel.publish(desiredWheelRotSpeedL)
		BR_wheel.publish(desiredWheelRotSpeedR)
		rate.sleep()
	rospy.spin()
