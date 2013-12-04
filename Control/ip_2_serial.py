#------------------------------------------------
# Author:	Alex Anderson
# Purpose:	Relay data sent by a client to
#			the MoCoBo through a serial port
# Date:		11/20/13
# Note:		This should be run on the robot
#			To exit the server close the console
#			The documenation for the serial library is here: http://pyserial.sourceforge.net/pyserial_api.html
#------------------------------------------------

import socket
import serial

class MyServer:
	#IP_port is the port used by the socket module to receive data from the client
	#COM_port is the COM_port used to by the serial module
	def __init__(self, IP_port, COM_port):
		HOST = ''		#signifies that this is a server
		PORT = IP_port
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		print "Created socket"
		s.bind((HOST, PORT))
		print "Bound port"
		s.listen(1)	#handle one client at a time(can do more than one?)
		
		ser = serial.Serial(COM_port)
		#ser.baudrate = 9600
		if ser.isOpen() == False:
			ser.open()
		
		while True:
			print "Listening to port"
			conn, addr = s.accept()
			print "Accepted a connection"
			print "Connected by", addr
			
			while True:
				try:
					data = conn.recv(1024)
					if data is not None and len(data) > 0:	#if we got good data
						print data	#print whatever the client sent
						
						if data == "take the highway":	#if the client signals it is leaving
							ser.write(" ")	#tell the MoCoMo to stop everything
							print "Client has signalled that it is leaving\n"
							conn.close()
							break
						else:
							ser.write(data)	#send whatever was sent to the MoCoBo
							
				except Exception as e:
					print "Something went wrong!"
					print e
					conn.close()
					break
							
if __name__ == "__main__":
	ser = MyServer(3612, 'COM10')