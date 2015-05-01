#!/usr/bin/env python
import rospy
import math
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64

# Author: Carl Smith

#create publishers
UL_wheel = rospy.Publisher('lunabot/UL_wheel', Float64, queue_size=1)
UR_wheel = rospy.Publisher('lunabot/UR_wheel', Float64, queue_size=1)
BL_wheel = rospy.Publisher('lunabot/BL_wheel', Float64, queue_size=1)
BR_wheel = rospy.Publisher('lunabot/BR_wheel', Float64, queue_size=1)
Bin_motor = rospy.Publisher('lunabot/Bin_motor', Float64, queue_size=1)

#initialize rotary encoder values
v_UL = 0.0
v_UR = 0.0
v_BL = 0.0
v_BR = 0.0

#handle joy initializing triggers to 0 instead of 1.0 (the release value)
l_trig_ready = False
r_trig_ready = False

#misc
bin_pos = 0.0
desiredPWM_L = 0.0
desiredPWM_R = 0.0
axis_x = 0.0
axis_y = 0.0
button_a = 0
button_b = 0
button_x = 0
button_y = 0
buttons = [0] * 11
l_trigger = 0.0		
r_trigger = 0.0
maxPWMval = 255.0	#pwm range is (0 - 255) forward or reverse
PWM_dx = maxPWMval/5.0
counter = 0

#callback functions
def callback_joy(data):
	global axis_x
	global axis_y
	global l_trigger
	global r_trigger
	global buttons
	global l_trig_ready
	global r_trig_ready
	buttons = data.buttons
	axis_x = data.axes[0]		 	#from -1 to 1
	axis_y = data.axes[1]		 	#from -1 to 1
	l_trigger = -1.0 * (data.axes[2]/2 - 0.5) #from 0 to 1
	r_trigger = -1.0 * (data.axes[5]/2 - 0.5)	#from 0 to 1
	#triggers will incorrectly equal 0.5 until pressed and released due to terrible joy initialization
	if l_trigger != 0.5 and l_trig_ready == False:
		l_trig_ready = True
	if r_trigger != 0.5 and r_trig_ready == False:
		r_trig_ready = True
	
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

#handles movement changes
def move():
	global desiredPWM_L
	global desiredPWM_R
	button_a = buttons[0]
	button_b = buttons[1]
	button_x = buttons[2]
	button_y = buttons[3]
	
	#wheel movement
	if l_trigger > 0.05 and l_trig_ready == True:	
		#zero turn left
		print 'zero left: ',desiredPWM_L," ",desiredPWM_R," ",l_trigger
		if desiredPWM_L - PWM_dx > -maxPWMval:
			#decrease
			desiredPWM_L = desiredPWM_L - PWM_dx * l_trigger
		else:
			#settle to min
			desiredPWM_L = -maxPWMval
		if desiredPWM_R + PWM_dx < maxPWMval:
			#increase
			desiredPWM_R = desiredPWM_R + PWM_dx * l_trigger
		else:
			#settle to max
			desiredPWM_R = maxPWMval
	elif r_trigger > 0.05 and r_trig_ready == True:	
		#zero turn right
		print 'zero right: ',desiredPWM_L," ",desiredPWM_R," ",r_trigger
		if desiredPWM_R - maxPWMval > -maxPWMval:
			#decrease
			desiredPWM_R = desiredPWM_R - PWM_dx * r_trigger
		else:
			#settle to min
			desiredPWM_R = -maxPWMval
		if desiredPWM_L + PWM_dx < maxPWMval:
			#increase
			desiredPWM_L = desiredPWM_L + PWM_dx * r_trigger
		else:
			#settle to max
			desiredPWM_L = maxPWMval
	elif axis_y > 0.05 or axis_y < -0.05:
		#if pressing forward or back
		print 'going forward or backward: ',desiredPWM_L," ",desiredPWM_R," ",axis_y
		if ((desiredPWM_L + PWM_dx * axis_y) > -maxPWMval) and ((desiredPWM_L + PWM_dx * axis_y) < maxPWMval):
			#increase or decrease
			desiredPWM_L = desiredPWM_L + PWM_dx * axis_y
		else:
			#settle to max or min
			desiredPWM_L = maxPWMval * axis_y/abs(axis_y)
		if ((desiredPWM_R + PWM_dx * axis_y) > -maxPWMval) and ((desiredPWM_R + PWM_dx * axis_y) < maxPWMval):
			#increase or decrease
			desiredPWM_R = desiredPWM_R + PWM_dx * axis_y
		else:
			#set value to max or min
			desiredPWM_R = maxPWMval * axis_y/abs(axis_y)
	else:
		print 'no input.. ',desiredPWM_L," ",desiredPWM_R
		#slow down left wheels to stop (0.0 is stationary value)
		if desiredPWM_L - PWM_dx > 0:		#if was going forward 
			#decrease
			desiredPWM_L = desiredPWM_L - PWM_dx
		elif desiredPWM_L + PWM_dx < 0:	#if was reversing
			#increase
			desiredPWM_L = desiredPWM_L + PWM_dx
		else:
			#settle to mid
			desiredPWM_L = 0.0
		#slow down right wheels to stop
		if desiredPWM_R - PWM_dx > 0:
			#decrease
			desiredPWM_R = desiredPWM_R - PWM_dx
		elif desiredPWM_R + PWM_dx < 0:
			#increase
			desiredPWM_R = desiredPWM_R + PWM_dx
		else:
			#settle to mid
			desiredPWM_R = 0.0
	#excavator and bin movement
	global bin_pos
	global counter
	#after rate*5 seconds, button can be pressed again
	if counter % 5 == 0:
		counter = 0
	#pressing B alternates target position
	if button_b == 1 and counter == 0:
		print "pressed button B: ",bin_pos
		if bin_pos == 0.0:
			bin_pos = 0.09
		elif bin_pos == 0.09:
			bin_pos = 0.0
		counter = 1
	elif counter != 0:
		counter = counter + 1
			
#main---------------------------------------------------
if __name__ == '__main__':
	rospy.init_node('controller')
	rospy.Subscriber("joy", Joy, callback_joy)
	rospy.Subscriber("/vrep/ULMotorData", JointState, callback_UL)
	rospy.Subscriber("/vrep/URMotorData", JointState, callback_UR)
	rospy.Subscriber("/vrep/BLMotorData", JointState, callback_BL)
	rospy.Subscriber("/vrep/BRMotorData", JointState, callback_BR)
	rate = rospy.Rate(10) # 10hz
	rate.sleep()
	while not rospy.is_shutdown():
		move()
		UL_wheel.publish(desiredPWM_L)
		UR_wheel.publish(desiredPWM_R)
		BL_wheel.publish(desiredPWM_L)
		BR_wheel.publish(desiredPWM_R)
		Bin_motor.publish(bin_pos)
		rate.sleep()
	rospy.spin()