#!/usr/bin/env python
import rospy
import math
from std_msgs.msg import Float64
from geometry_msgs.msg import Point32

# Author: Carl Smith

#create publishers
UL_IR = rospy.Publisher('lunabot/UL_IR', Float64, queue_size=1)
UR_IR = rospy.Publisher('lunabot/UR_IR', Float64, queue_size=1)
BL_IR = rospy.Publisher('lunabot/BL_IR', Float64, queue_size=1)
BR_IR = rospy.Publisher('lunabot/BR_IR', Float64, queue_size=1)

#vrep IR sub callback functions
def callback_UL(pkg):
	print "UL_IR: "
	proximity = pkg.detectedPoint.z
	UL_IR.publish(proximity)

def callback_UR(pkg):
	print "UR_IR: "
	proximity = pkg.detectedPoint.z
	UR_IR.publish(proximity)

def callback_BL(pkg):
	print "BL_IR: "
	proximity = pkg.detectedPoint.z
	BL_IR.publish(proximity)

def callback_BR(pkg):
	print "BR_IR: "
	proximity = pkg.detectedPoint.z
	BR_IR.publish(proximity)

#main-------------------------------------------------------------
if __name__ == '__main__':
	rospy.init_node('IR_handler')
	#subscribe to vrep IR topics
	rospy.Subscriber("/vrep/UL_proximity_sensor", Point32, callback_UL)
	rospy.Subscriber("/vrep/UR_proximity_sensor", Point32, callback_UR)
	rospy.Subscriber("/vrep/BL_proximity_sensor", Point32, callback_BL)
	rospy.Subscriber("/vrep/BR_proximity_sensor", Point32, callback_BR)
	rospy.spin()
