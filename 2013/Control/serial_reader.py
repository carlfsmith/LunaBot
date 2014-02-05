#---------------------------------------
# Author:	Alex Anderson
# Purpose:	Display what is sent to
#			a specific serial port
# Date:		12/3/13
# Note:		The documentation for the
#			serial library is here: http://pyserial.sourceforge.net/pyserial_api.html
# --------------------------------------


import serial

class EchoSerial():
	def run(self, port):
		self.ser = serial.Serial(port)
		
		if self.ser.isOpen() == False:
			self.ser.open()		
			
		while True:
			data = self.ser.read()
			if data is not None:
				print data
				
if __name__ == "__main__":
	ec = EchoSerial()
	ec.run('COM9')