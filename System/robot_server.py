'''
Created on Apr 27, 2013

@author: shook

This python script runs on the lunabot.
'''

import socket
import os
import threading
import Queue

class ServerControl(threading.Thread):
    HOST = ''
    PORT = 1337
    BACKLOG = 2
    SIZE = 2
    
    def __init__(self, robot):
        threading.Thread.__init__(self)
        self.robot = robot
        
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((self.HOST, self.PORT))
        self.sock.listen(self.BACKLOG)
        
        self._running = True
        self.client = None
        
        self.queue = Queue.Queue()
     
    def run(self):        
        while self._running:
            print 'Listening for connections...'
            self.client, address = self.sock.accept()
            print 'Client Connected:', address
            self._message_loop()
            
    def _message_loop(self):
        connected = True
        while connected:
#            self.client.setblocking(0)
            try:
                data = self.client.recv(self.SIZE)
                print 'network data:', data
                if data:
                    # send confirmation response
                    self.client.send('A')
                    
                    # start thread to process command
                    cmd_thread = ProcessingThread(self.queue,self.robot)
                    cmd_thread.setDaemon(True)
                    cmd_thread.start()
                    self.queue.put(str(data))
                    
                else:
                    print 'Controller Disconnected'
                    connected = False
                    self.client.close()
                    self._all_motors_off()
            except Exception as e:
                print 'Server error:', e
                

class ProcessingThread(threading.Thread):
    def __init__(self, queue,robot):
        threading.Thread.__init__(self)
        self.queue = queue
        self.robot = robot
        
    def run(self):
        while True:
            data = self.queue.get()
            if data == 'A' or data == 'W' or data == 'S' or data == 'D' or data == 'Q' or data == 'Z' or data == 'C':
                self.robot.set_robot_state(data)
            elif data == 'O' or data == 'K' or data == 'N':
                self.robot.set_arm_state(data)
            elif data == 'P' or data == 'L' or data == 'M':
                self.robot.set_bucket_state(data) 
            elif data == '7':
                self.robot.FORWARD_SPEED += 1
            elif data == '4':
                self.robot.FORWARD_SPEED -= 1
            elif data == '8':
                self.robot.BACK_SPEED += 1
            elif data == '5':
                self.robot.BACK_SPEED -= 1
            elif data == '9':
                self.robot.TURN_SPEED += 1
            elif data == '6':
                self.robot.TURN_SPEED -= 1
            elif data == 'R':
                self.robot.controller.reset_mocobo()
            elif data.count('G') > 0:
                # shutdown
                print 'shutting down system'
                os.system("shutdown -s -f -t 0")
            self.queue.task_done()
