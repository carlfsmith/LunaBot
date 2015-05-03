#!/usr/bin/env python
import rospy
import math
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64

# Author: Carl Smith

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
stop_flag_F = False
stop_flag_B = False

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
	
def callback_wEncoder_UL(data):
	global v_UL
	v_UL = data.velocity[0]
	#print "in UL", v_UL

def callback_wEncoder_UR(data):
	global v_UR
	v_UR = data.velocity[0]
	#print "in UR", v_UR

def callback_wEncoder_BL(data):
	global v_BL
	v_BL = data.velocity[0]
	#print "in BL", v_BL

def callback_wEncoder_BR(data):
	global v_BR
	v_BR = data.velocity[0]
	#print "in BR", v_BR

def callback_IR_UL(proximity):
	global stop_flag_UL
	if distance(proximity.data) < 0.5:
		stop_flag_F = True
	else:
		stop_flag_F = False

def callback_IR_UR(proximity):
	global stop_flag_UR
	if distance(proximity.data) < 0.5:
		stop_flag_F = True
	else:
		stop_flag_F = False

def callback_IR_BL(proximity):
	global stop_flag_BL
	if distance(proximity.data) < 0.5:
		stop_flag_B = True
	else:
		stop_flag_B = False

def callback_IR_BR(proximity):
	global stop_flag_BR
	if distance(proximity.data) < 0.5:
		stop_flag_B = True
	else:
		stop_flag_B = False

#finds distance given IR proximity reading
def distance(proximity):
	d1 = proximity * math.cos(math.radians(10.0))	#distance projected directly forward-down
	d2 = proximity * math.cos(math.radians(82.0))	#height of sensor
	distance = math.sqrt(d1*d1 + d2*d2)	#direct forward distance
	print proximity," ",d1," ",d2," ",distance
	return distance

#handles slowing down to stop
def stop():
	global desiredPWM_L
	global desiredPWM_R

	#slow down left wheels to stop (0.0 is stationary value)
	if desiredPWM_L - PWM_dx > 0:		#if robot was going forward 
		#decrease
		desiredPWM_L = desiredPWM_L - PWM_dx
	elif desiredPWM_L + PWM_dx < 0:		#if robot was reversing
		#increase
		desiredPWM_L = desiredPWM_L + PWM_dx
	else:
		#settle to mid
		desiredPWM_L = 0.0
	#slow down right wheels to stop
	if desiredPWM_R - PWM_dx > 0:		#if robot was going forward 
		#decrease
		desiredPWM_R = desiredPWM_R - PWM_dx
	elif desiredPWM_R + PWM_dx < 0:		#if robot was reversing
		#increase
		desiredPWM_R = desiredPWM_R + PWM_dx
	else:
		#settle to mid
		desiredPWM_R = 0.0

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
		#print 'zero left: ',desiredPWM_L," ",desiredPWM_R," ",l_trigger
		if desiredPWM_L - PWM_dx > -maxPWMval * l_trigger:
			#decrease
			desiredPWM_L = desiredPWM_L - PWM_dx
		else:
			#settle to min
			desiredPWM_L = -maxPWMval * l_trigger
		if desiredPWM_R + PWM_dx < maxPWMval * l_trigger:
			#increase
			desiredPWM_R = desiredPWM_R + PWM_dx
		else:
			#settle to max
			desiredPWM_R = maxPWMval * l_trigger
	elif r_trigger > 0.05 and r_trig_ready == True:	
		#zero turn right
		#print 'zero right: ',desiredPWM_L," ",desiredPWM_R," ",r_trigger
		if desiredPWM_R - PWM_dx > -maxPWMval * r_trigger:
			#decrease
			desiredPWM_R = desiredPWM_R - PWM_dx
		else:
			#settle to min
			desiredPWM_R = -maxPWMval * r_trigger
		if desiredPWM_L + PWM_dx < maxPWMval * r_trigger:
			#increase
			desiredPWM_L = desiredPWM_L + PWM_dx
		else:
			#settle to max
			desiredPWM_L = maxPWMval * r_trigger
	elif axis_y > 0.05 and stop_flag_F == False:
		#if pressing forward
		#print 'going forward: ',desiredPWM_L," ",desiredPWM_R," ",axis_y
		if desiredPWM_L + PWM_dx < maxPWMval * axis_y:
			#increase left wheels
			desiredPWM_L = desiredPWM_L + PWM_dx
		else:
			#settle left wheels
			desiredPWM_L = maxPWMval
		if desiredPWM_R + PWM_dx < maxPWMval * axis_y:
			#increase right wheels
			desiredPWM_R = desiredPWM_R + PWM_dx
		else:
			#settle right wheels
			desiredPWM_R = maxPWMval
	elif axis_y < -0.05 and stop_flag_B == False:
		#if pressing back
		#print 'going backward: ',desiredPWM_L," ",desiredPWM_R," ",axis_y
		if desiredPWM_L - PWM_dx > maxPWMval * axis_y:
			#decrease left wheels
			desiredPWM_L = desiredPWM_L - PWM_dx
		else:
			#settle left wheels
			desiredPWM_L = -maxPWMval
		if desiredPWM_R - PWM_dx > maxPWMval * axis_y:
			#decrease right wheels
			desiredPWM_R = desiredPWM_R - PWM_dx
		else:
			#settle right wheels
			desiredPWM_R = -maxPWMval
	else:
		#print 'no input.. ',desiredPWM_L," ",desiredPWM_R
		stop()
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
	#publish wheel pwm information
	UL_wheel = rospy.Publisher('lunabot/UL_wheel', Float64, queue_size=1)
	UR_wheel = rospy.Publisher('lunabot/UR_wheel', Float64, queue_size=1)
	BL_wheel = rospy.Publisher('lunabot/BL_wheel', Float64, queue_size=1)
	BR_wheel = rospy.Publisher('lunabot/BR_wheel', Float64, queue_size=1)
	Bin_motor = rospy.Publisher('lunabot/Bin_motor', Float64, queue_size=1)
	#subscribe to joystick, vrep wheel jointstate and IR proximity information 
	rospy.Subscriber("joy", Joy, callback_joy)
	rospy.Subscriber("/vrep/ULMotorData", JointState, callback_wEncoder_UL)
	rospy.Subscriber("/vrep/URMotorData", JointState, callback_wEncoder_UR)
	rospy.Subscriber("/vrep/BLMotorData", JointState, callback_wEncoder_BL)
	rospy.Subscriber("/vrep/BRMotorData", JointState, callback_wEncoder_BR)
	rospy.Subscriber("lunabot/UL_IR", Float64, callback_IR_UL)
	rospy.Subscriber("lunabot/UR_IR", Float64, callback_IR_UR)
	rospy.Subscriber("lunabot/BL_IR", Float64, callback_IR_BL)
	rospy.Subscriber("lunabot/BR_IR", Float64, callback_IR_BR)
	#set loop rate and main loop
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
