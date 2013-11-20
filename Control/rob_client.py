# -----------------------
# Author:	Alex Anderson
# Purpose:	Send commands to server on robot
#			with IP address of self.HOST based 
#			on the activity of the hat/D-pad of
#			a joystick/gamepad
# Date:		11/20/13
# ---------------------------

import socket
from gamepad_manager import *	#gampad_manager must be in the same directory as this script

#  Takes joystick/gamepad input and
#sends the appropriate signals to the
#robot with the Robot_Client class
class Robot_Commander:
	def __init__(self, port=3612):
		self.bot_link = Robot_Client(port)
		self.bot_link.connect()
		
		self.drive_direction = Drive_Signals()
		
		self.gm = GamepadManager()

	def drive(self, direction):
		if self.drive_direction != direction:
			self.drive_direction = direction
			self.bot_link.send_command(str(direction))

	def main(self):
		num_sticks = self.gm.get_num_sticks()
		
		if num_sticks == 0:
			found = False
			while found == False:
				print "Now joysticks/gamepads could be found :( Look again? (y/n)? ",
				if raw_input() == 'y':
					num_sticks = self.gm.search()
					if  num_sticks > 0:						
						found = True
				else:
					print "Are you sure you want to exit the client? (y/n) ",
					if raw_input() == 'y':
						return
		
		#Ask user to choose a gamepad/joystick
		print "Select which Gamepad/Joystick to use."
		i = 0
		while i < num_sticks:
			print "\t%d: %s" % (i, self.gm.get_stick_name(i))
			i = i + 1
		
		#Ensure that the id number is a valid number
		stk_id = int(raw_input())
		while stk_id < 0 or stk_id > (num_sticks-1):
			print "That isn't a valid id number. Try again ",
			stk_id = int(raw_input())
		
		#main control loop
		drive_dir = self.drive_direction
		while True:
			self.gm.update_events()
			
			#decide which way to drive
			hat_vect = self.gm.get_hat(0, stk_id)
			if hat_vect == (0,0):	#stop
				drive_dir = Drive_Signals.STOP
			elif hat_vect == (0,1):	#forward
				drive_dir = Drive_Signals.FORWARD
			elif hat_vect == (0,-1):	#backward
				drive_dir = Drive_Signals.BACKWARD
			elif hat_vect == (1,0):	#right
				drive_dir = Drive_Signals.RIGHT
			elif hat_vect == (-1,0):	#left
				drive_dir = Drive_Signals.LEFT
						
			if self.gm.get_button(6, stk_id) == True and self.gm.get_button(7, stk_id) == True:
				break
			
			self.drive(drive_dir)
			
	def __del__(self):
		self.bot_link.disconnect()

#  Creates/Maintains connection with server on robot		
class Robot_Client:
	def __init__(self, port=3612):
		self.HOST = 'localhost'	#IP address of robot/server
		self.PORT = port
		self.SIZE = 1024
		
		self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		
		self.robot_connected = False
		
	def connect(self):
		#Creates connection with the robot
		print "Connecting to robot...",
		try:
			self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.socket.connect((self.HOST, self.PORT))
			self.robot_connected = True
			print "\tConnected!"
		except Exception as e:
			print "\tConnection failed!"
			print e

	def send_command(self, cmd):
		#if robot is connected send a command
		if self.robot_connected:
			try:
				self.socket.send(cmd)
			except:
				self.disconnect_robot()
		
	def disconnect(self):
		#Closes the connection with the robot
		print "Disconnecting...",
		if self.robot_connected == True:
			self.socket.send("Take the highway")
			self.socket.close()
			self.robot_connected = False
		
		print "\tDisconnected!"

#  Contains variables which hold the signals
#to be sent to the robot for drive direction
class Drive_Signals:
	STOP = 'Z'
	FORWARD = 'W'
	BACKWARD = 'S'
	LEFT = 'A'
	RIGHT = 'D'

#  Only perform this if execution
#started in this script
if __name__ == "__main__":
	rc = Robot_Commander(3612)
	rc.main()
