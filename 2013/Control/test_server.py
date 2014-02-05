#----------------------------------
# Author:	Alex Anderson
# Purpose:	Setup a simple server to
#			Test client programs
# Date:		11/20/13
# Note:		The default port is 3612.
#			To exit the server close the console
#------------------------------------

import socket

class MyServer:
	def __init__(self):
		HOST = ''		#signifies that this is a server
		PORT = 3612
		s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		print "Created socket"
		s.bind((HOST, PORT))
		print "Bound port"
		s.listen(1)	#handle one client at a time(can do more than one)
		
		while True:
			print "Listening to port"
			conn, addr = s.accept()
			print "Accepted a connection"
			print "Connected by", addr
			
			while True:
				data = conn.recv(1024)
				if data is not None and len(data) > 0:	#if we got good data
					print data	#print whatever the client sent
					if data == "Take the highway":
						print "Client has signalled that it is leaving\n"
						conn.close()
						break

if __name__ == "__main__":
	ser = MyServer()
