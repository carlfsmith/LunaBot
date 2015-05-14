#!/usr/bin/env python
import rospy
import math
from geometry_msgs.msg import Twist
from sensor_msgs.msg import Joy
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64

# Author: Carl Smith

#handle joy initializing triggers to 0 instead of 1.0 (the release value)
l_trig_ready = False
r_trig_ready = False
#initialize IR flag values: front, back
stop_flag_F = False
stop_flag_B = False
#misc
desiredPWM_L = 0.0
desiredPWM_R = 0.0
axis_x = 0.0
axis_y = 0.0
l_trigger = 0.0		
r_trigger = 0.0
button_L1 = 0
button_R1 = 0
maxPWMval = 255.0	#pwm range is (0 - 255) forward or reverse
PWM_dx = maxPWMval/10.0
stop_flag_F = False
stop_flag_B = False
t_angle = 0	#holds angle to turn robot
p_angle = 0.0 #holds gyroscope value of robot orientation before a turn was initiated
c_angle = 0.0 #holds current orientation
turning_L = False
turning_R = False
counter = 0

#callback functions
def callback_joy(data):
	global axis_x
	global axis_y
	global l_trigger
	global r_trigger
	global button_R1
	global button_L1
	global l_trig_ready
	global r_trig_ready
	axis_x = data.axes[0]		 	#from -1 to 1
	axis_y = data.axes[1]		 	#from -1 to 1
	button_L1 = data.buttons[3]		#fron 0 to 1
	button_R1 = data.buttons[4]		#from 0 to 1
	l_trigger = -1.0 * (data.axes[2]/2 - 0.5) #from 0 to 1
	r_trigger = -1.0 * (data.axes[5]/2 - 0.5)	#from 0 to 1
	#triggers will incorrectly equal 0.5 until pressed and released due to terrible joy initialization
	if l_trigger != 0.5 and l_trig_ready == False:
		l_trig_ready = True
	if r_trigger != 0.5 and r_trig_ready == False:
		r_trig_ready = True

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
	global stop_flag_B
	if distance(proximity.data) < 0.5:
		stop_flag_B = True
	else:
		stop_flag_B = False

def callback_IR_BR(proximity):
	global stop_flag_B
	#print distance(proximity.data)
	if distance(proximity.data) < 0.5:
		stop_flag_B = True
	else:
		stop_flag_B = False

#finds distance given IR proximity reading
def distance(proximity):
	d1 = proximity * math.cos(math.radians(10.0))	#distance projected directly forward-down
	d2 = proximity * math.cos(math.radians(82.0))	#height of sensor
	distance = math.sqrt(d1*d1 + d2*d2)	#direct forward distance
	#print proximity," ",d1," ",d2," ",distance
	return distance

#handles movement changes
def move():
	global desiredPWM_L
	global desiredPWM_R
	global turning_L
	global turning_R
	global counter
	#after rate*5 seconds, buttons can be pressed again
	if counter % 5 == 0:
		counter = 0
	#change angle of turn on button press only when not turning; range is 5-30 degrees
	if counter == 0 and turning_L == False and turning_R == False:
		if button_L1 == True and t_angle > math.pi/36:	#5 degrees in radians
			t_angle = t_angle + math.pi / 36	#5 degrees in radians
		elif button_R1 == True and t_angle < math.pi/6:	#30 degrees in radians
			t_angle = t_angle - math.pi / 36	#5 degrees in radians
		counter = 1
	elif counter != 0:
		counter = counter + 1
	#detect trigger presses and allow for turning
	if l_trigger > 0.05 and l_trig_ready == True and turning_L == False:
		turning_L == True
		turning_R == False
	elif r_trigger > 0.05 and r_trig_ready == True and turning_R == False:
		turning_R == True
		turning_L == False
	#use gyroscope to see if robot has turned as many degrees as required in order to stop turning
	##do stuff to get c_angle value from sensor
	angle = (c_angle - p_angle + math.pi) % 2*math.pi - math.pi
	if turning_L == True or turning_R == True and angle >= t_angle:
		turning_L == False
		turning_R == False
	#wheel control
	if turning_L == True:
		#zero turn left
		#print 'zero left: ',desiredPWM_L," ",desiredPWM_R," ",l_trigger
		if desiredPWM_L - PWM_dx > -maxPWMval:
			#decrease
			desiredPWM_L = desiredPWM_L - PWM_dx
		else:
			#settle to min
			desiredPWM_L = -maxPWMval
		if desiredPWM_R + PWM_dx < maxPWMval:
			#increase
			desiredPWM_R = desiredPWM_R + PWM_dx
		else:
			#settle to max
			desiredPWM_R = maxPWMval
	if turning_R == True:	
		#zero turn right
		#print 'zero right: ',desiredPWM_L," ",desiredPWM_R," ",r_trigger
		if desiredPWM_R - PWM_dx > -maxPWMval:
			#decrease
			desiredPWM_R = desiredPWM_R - PWM_dx
		else:
			#settle to min
			desiredPWM_R = -maxPWMval
		if desiredPWM_L + PWM_dx < maxPWMval:
			#increase
			desiredPWM_L = desiredPWM_L + PWM_dx
		else:
			#settle to max
			desiredPWM_L = maxPWMval
	elif axis_y > 0.05 and stop_flag_F == False:
		#if pressing forward
		print 'going forward: ',desiredPWM_L," ",desiredPWM_R," ",axis_y
		#print 'going forward: ',desiredPWM_L," ",desiredPWM_R," ",axis_y
		if desiredPWM_L + PWM_dx < maxPWMval * axis_y:
			#increase left wheels
			desiredPWM_L = desiredPWM_L + PWM_dx
		else:
			#settle left wheels
			desiredPWM_L = maxPWMval * axis_y
		if desiredPWM_R + PWM_dx < maxPWMval * axis_y:
			#increase right wheels
			desiredPWM_R = desiredPWM_R + PWM_dx
		else:
			#settle right wheels
			desiredPWM_R = maxPWMval * axis_y
	elif axis_y < -0.05 and stop_flag_B == False:
		#print stop_flag_B
		#if pressing back
		print 'going backward: ',desiredPWM_L," ",desiredPWM_R," ",axis_y
		if desiredPWM_L - PWM_dx > maxPWMval * axis_y:
			#decrease left wheels
			desiredPWM_L = desiredPWM_L - PWM_dx
		else:
			#settle left wheels
			desiredPWM_L = maxPWMval * axis_y

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
			desiredPWM_R = maxPWMval * axis_y
			desiredPWM_R = -maxPWMval
	else:
		#slow down left wheels to stop (0.0 is stationary value)
		#print 'no input.. ',desiredPWM_L," ",desiredPWM_R
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
			
#main---------------------------------------------------
if __name__ == '__main__':
	rospy.init_node('controller')
	#publish wheel pwm information
	UL_wheel = rospy.Publisher('lunabot/UL_wheel', Float64, queue_size=1)
	UR_wheel = rospy.Publisher('lunabot/UR_wheel', Float64, queue_size=1)
	BL_wheel = rospy.Publisher('lunabot/BL_wheel', Float64, queue_size=1)
	BR_wheel = rospy.Publisher('lunabot/BR_wheel', Float64, queue_size=1)
	Bin_motor = rospy.Publisher('lunabot/Bin_motor', Float64, queue_size=1)
	Ex_motor = rospy.Publisher('lunabot/Ex_motor', Float64, queue_size=1)
	Bucket_motor = rospy.Publisher('lunabot/Bucket_motor', Float64, queue_size=1)
	#subscribe to joystick, vrep wheel jointstate and IR proximity information 
	rospy.Subscriber("joy", Joy, callback_joy)
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
		Ex_motor.publish(ex_pos)
		Bucket_motor.publish(buck_vel)
		rate.sleep()
	rospy.spin()
