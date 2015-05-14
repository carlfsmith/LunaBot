#!/usr/bin/env python
import rospy
import math
from std_msgs.msg import Float64
from geometry_msgs.msg import Point32
from vrep_common.msg import ProximitySensorData	#added when integrating vrep_ros_bridge

# Author: Carl Smith

#vrep IR sub callback functions
def callback_UL(pkg):
	proximity = pkg.detectedPoint.z
	UL_IR.publish(proximity)
	#print "UL_IR: ",proximity

def callback_UR(pkg):
	proximity = pkg.detectedPoint.z
	UR_IR.publish(proximity)
	#print "UR_IR: ",proximity

def callback_BL(pkg):
	proximity = pkg.detectedPoint.z
	BL_IR.publish(proximity)
	#print "BL_IR: ",proximity

def callback_BR(pkg):
	proximity = pkg.detectedPoint.z
	BR_IR.publish(proximity)
	#print "BR_IR: ",proximity

#main-------------------------------------------------------------
if __name__ == '__main__':
	rospy.init_node('IR_handler')
	#publish proximity float64 information
	UL_IR = rospy.Publisher('lunabot/UL_IR', Float64, queue_size=1)
	UR_IR = rospy.Publisher('lunabot/UR_IR', Float64, queue_size=1)
	BL_IR = rospy.Publisher('lunabot/BL_IR', Float64, queue_size=1)
	BR_IR = rospy.Publisher('lunabot/BR_IR', Float64, queue_size=1)
	#subscribe to vrep IR topics
	rospy.Subscriber("/vrep/UL_proximity_sensor", ProximitySensorData, callback_UL)
	rospy.Subscriber("/vrep/UR_proximity_sensor", ProximitySensorData, callback_UR)
	rospy.Subscriber("/vrep/BL_proximity_sensor", ProximitySensorData, callback_BL)
	rospy.Subscriber("/vrep/BR_proximity_sensor", ProximitySensorData, callback_BR)
	rospy.spin()
