# ------------------------------------------------------
# Author:	Alex Anderson
# Purpose:	Send commands to server on robot
#			based on the activity of a joystick/gamepad
# Date:		12/3/13
# Note:		Sends commands as ?## where ? is an ASCII
#			character and ## is a percentage with 0
#			being backward, 100 forward, and 50 stop
# -----------------------------------------------------

import sys
import socket
from gamepad_manager import *	#gampad_manager.py must be in the same directory as this script

#  Takes joystick/gamepad input and
#sends the appropriate signals to the
#robot with the Robot_Client class
class Robot_Commander:
	#host:	set host to a string representing the robot's IP address
	#port:	set the port to send data
	def __init__(self, host="localhost", port=3612):
		self.bot_link = Robot_Client(host, port)
		self.bot_link.connect()
		
		self.mech_drive_direction = Mech_Drive_Signals()
		
		self.tank_left_per = 50
		self.tank_right_per = 50
		
		self.gm = GamepadManager()
		
		self.fancy_drive = False
		self.drive_swap_flag = False

	def mech_drive(self, direction, per):
		if self.mech_drive_direction != direction:
			self.mech_drive_direction = direction
			self.bot_link.send_command(direction + str(per))
	
	def tank_drive_left(self, per):
		if self.tank_left_per != per:
			self.tank_left_per = per
			self.bot_link.send_command('F' + str(per))
	
	def tank_drive_right(self, per):
		if self.tank_right_per != per:
			self.tank_right_per = per
			self.bot_link.send_command('G' + str(per))
	
	def swap_drive_styles(self, swap):
		if self.drive_swap_flag == False and swap == True:
			self.drive_swap_flag = True
			self.fancy_drive = not self.fancy_drive
		
		if swap == False:
			self.drive_swap_flag = False

	def stop(self):
		self.bot_link.send_command(" 50")
	
	def disconnect(self):
		self.bot_link.disconnect()
	
	#where the fun stuff happens
	def main(self):
		self.stop()	#tell the robot to stop while we get ready
		
		num_sticks = self.gm.get_num_sticks()
		
		#if there are no gamepads/joysticks... we must find one...
		#we cannot advance without one
		if num_sticks == 0:
			found = False
			while found == False:
				print "No new joysticks/gamepads could be found :( Look again? (y/n)? ",
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
		drive_dir = self.mech_drive_direction
		drive_per = 50
		while True:
			self.gm.update_events()
			
			if self.fancy_drive == True:
				#decide which way to drive
				hat_vect = self.gm.get_hat(0, stk_id)
				if hat_vect == (0,0):	#stop
					drive_dir = Mech_Drive_Signals.STOP
					drive_per = 50
				#	Straight lines
				elif hat_vect == (0,1):	#forward
					drive_dir = Mech_Drive_Signals.STRAIGHT
					drive_per = 60
				elif hat_vect == (0,-1):	#backward
					drive_dir = Mech_Drive_Signals.STRAIGHT
					drive_per = 40
				elif hat_vect == (1,0):	#right
					drive_dir = Mech_Drive_Signals.SIDE
					drive_per = 60
				elif hat_vect == (-1,0):	#left
					drive_dir = Mech_Drive_Signals.SIDE
					drive_per = 40
				#	Diagonal lines
				elif hat_vect == (-1,1):	#front left
					drive_dir = Mech_Drive_Signals.BACK_SLASH
					drive_per = 60
				elif hat_vect == (1,1):	#front right
					drive_dir = Mech_Drive_Signals.FOR_SLASH
					drive_per = 60
				elif hat_vect == (1,-1):	#back rigiht
					drive_dir = Mech_Drive_Signals.BACK_SLASH
					drive_per = 40
				elif hat_vect == (-1,-1):	#back right
					drive_dir = Mech_Drive_Signals.FOR_SLASH
					drive_per = 40
				
				#Rotate
				if self.gm.get_button(4, stk_id) == True:	#Counter Clockwise
					drive_dir = Mech_Drive_Signals.ROTATE
					drive_per = 40
				elif self.gm.get_button(5, stk_id) == True:	#Clockwise
					drive_dir = Mech_Drive_Signals.ROTATE
					drive_per = 60
								
				self.mech_drive(drive_dir, drive_per)
				
			else:	#Tank Drive
				#Left side drive control
				if self.gm.get_axis(1, stk_id) == 0:
					self.tank_drive_left(50)
				elif self.gm.get_axis(1, stk_id) > 0:
					self.tank_drive_left(40)
				elif self.gm.get_axis(1, stk_id) < 0:
					self.tank_drive_left(60)
				
				#Right side drive control
				if self.gm.get_axis(3, stk_id) == 0:
					self.tank_drive_right(50)
				elif self.gm.get_axis(3, stk_id) > 0:
					self.tank_drive_right(40)
				elif self.gm.get_axis(3, stk_id) < 0:
					self.tank_drive_right(60)

			#toggle between mech and tank mode (this is the start button on my controller)
			if self.gm.get_button(9, stk_id) == True:
				self.swap_drive_styles(True)
			else:
				self.swap_drive_styles(False)
			
			#Stop the robot and disconnect
			if self.gm.get_button(6, stk_id) == True and self.gm.get_button(7, stk_id) == True:
				self.stop()
				self.disconnect()
				break

	def __del__(self):
		self.bot_link.disconnect()

#  Creates/Maintains connection with server on robot		
class Robot_Client:
	def __init__(self, host="localhost", port=3612):
		self.HOST = host	#IP address of robot/server
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
			self.socket.send("take the highway")
			self.socket.close()
			self.robot_connected = False
		
		print "\tDisconnected!"

#  Contains variables which hold the signals
#to be sent to the robot for drive direction
class Mech_Drive_Signals:
	#Drive Directions
	STOP = ' '
	STRAIGHT = 'E'
	SIDE = 'U'
	BACK_SLASH = 'H'
	FOR_SLASH = 'I'
	
	#Turning
	ROTATE = 'J'

#  Only perform this if execution
#started in this script
if __name__ == "__main__":
	rc = Robot_Commander()
	rc.main()