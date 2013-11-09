#---------------------------------------------------------------
# Author:	Alex Anderson
# Purpose:	Look for all available joysticks and/or gampads and
#			make them easy to access. Also allows for testing
#			gamepads if run individually through calling _main()
# Date:		11/6/13
# Note:		This is based on joystick_example.py which I found
#			on the pygame website.
#---------------------------------------------------------------

import pygame
from pygame.locals import *
import math
 
class GamepadManager:
	#constructor
	#db:	size of the deadband (+/-) for the axis of the analog sticks
	def __init__(self, db = 0.125):
		print "Initializing pygame..."
		pygame.init()
		 
		#Search for gamepads
		print "Searching for Gamepads..."
		pygame.joystick.init()
 
		self.sticks = []	#list of pygame.joystick objects
		self.stick_names = []	#list of the names of the joysticks
		
		print "Found %d gamepads" % (pygame.joystick.get_count())
		
		#Initialize all gamepads
		print "Initializing Gamepads..."
		for i in range(0, pygame.joystick.get_count()):
			self.sticks.append(pygame.joystick.Joystick(i))
			self.stick_names.append(self.sticks[i].get_name())
			self.sticks[i].init()
 
		print self.stick_names
		
		self.db = db
	
	#return the number of gamepads/joysticks that have been found
	#return:	number of sticks that have been found
	def get_num_sticks(self):
		return len(self.sticks)
	
	#find the id number of a joystick/gamepad by name
	#return:	id number of the given gamepad/joystick, returns -1 if not found
	def get_stick_id(self, name):
		i = 0
		while i < len(self.stick_names):
			if name == self.stick_names[i]:
				return i
			i = i + 1
		return -1
	
	#default:	returns a list of names of all gamepads/joysticks
	#else:		returns the name of gamepad/joystick specified
	#returns None if id is out of range
	def get_stick_name(self, id=-1):
		if id == -1:
			return self.stick_names
		elif id < self.get_num_sticks():
			return self.stick_names[id]
	
	#get the value of one of the axis
	#axis_num: 	id # of the axis desired
	#stick_num:	id # of the desired joystick/gamepad chooses first gampad by default
	#return:	values between -1.0 and 1.0, deadband is taken into account
	def get_axis(self, axis_num, stick_num=0):
		if (self.sticks[stick_num]):	#if this is a valid gamepad
			if (axis_num < self.sticks[stick_num].get_numaxes()):	#if this is a valid axis
				val = self.sticks[stick_num].get_axis(axis_num)
				if math.fabs(val) > self.db:	#if the value is outside of the deadband
					return val
 
		return 0
 
	#get the current state of one of the buttons
	#b_num:		id # of the desired button
	#stick_num:	id # of the desired joystick/gamepad chooses first gampad by default
	#return:	True if the button is pressed
	def get_button(self, b_num, stick_num=0):
		if (self.sticks[stick_num]):	#if this is a valid gamepad
			if (b_num < self.sticks[stick_num].get_numbuttons()):	#if this is a valid button
				return self.sticks[stick_num].get_button(b_num)
 
		return False
 
	#get the value of the hat
	#hat_num:	id # of the desired hat
	#stick_num:	id # of the desired joystick/gamepad chooses first gamepad by default
	#return:	tuple/array describing the hat's position
	def get_hat(self, hat_num, stick_num=0):
		if (self.sticks[stick_num]):	#if this is a valid gamepad
			if (hat_num < self.sticks[stick_num].get_numhats()):	#if this is a valid hat
				return self.sticks[stick_num].get_hat(hat_num)
 
		return (0, 0)
	
	#test joystick/gamepad to ensure that it works
	#prints the values to the screen
	#should only be used to test whether the gamepad works
	def _main(self, stick_num=0):
		#This stuff is required so pygame can get at the keyboard
		#This is not required for the operation of the class
		pygame.display.init()
		screen = pygame.display.set_mode((150, 25))
		font = pygame.font.SysFont("Courier", 12)
		screen.fill(0)
		screen.blit(font.render("Press SPACE to exit", True, (255, 255, 255), (0, 0, 0)), (0, 0))
		pygame.display.flip()
		
		#here is the stuff that looks at the gamepad data
		done = False
		while not done:
			keys = pygame.event.get()	#gets keyboard and joystick/gamepad data
			
			#stop if the space bar is pressed
			for event in keys:
				if event.type == KEYDOWN and event.key == K_SPACE:
					done = True
				elif event.type == QUIT:
					done = True

			#get axis data
			axis = ""
			for i in range(0, self.sticks[stick_num].get_numaxes()):
				ax_val = self.get_axis(i, stick_num)
				if ax_val:
					axis = axis + ( "Axis #%d = %.2f" % (i, ax_val) )
			#print axis data if the is any to print
			if len(axis) > 0:
				print axis
			
			#get button data
			b_pressed = []
			for i in range(0, self.sticks[stick_num].get_numbuttons()):
				if (self.get_button(i, stick_num)):
					b_pressed.append(i)
			#print button data if there is any to print
			if len(b_pressed) > 0:
				print b_pressed,
				print " are true"

			for i in range(0, self.sticks[stick_num].get_numhats()):
				vect = self.get_hat(i, stick_num)	#returns (x, y) "vector"
				if (vect != (0, 0)):
					print vect

 		
if __name__ == "__main__":
	g_pad = GamepadManager()
	g_pad._main()