import time
import serial

class RobotController():
    
    # Motor 0: FL
    # Motor 1: FR
    # Motor 2: BL
    # Motor 3: BR
    channel_dict = {0:'A', 1:'B', 2:'C', 3:'D'}
    
    def __init__(self, robot):
        self.robot = robot
        self.ser = None
        
    def _make_serial_connection(self):
        print 'Opening motor board serial connection...'
        
        SERIAL_PORT = 'COM4'
        #SERIAL_PORT = 'COM13'
        SERIAL_PORT2 = 'COM5'
        SERIAL_RATE = 9600
        
        try:
            self.ser = serial.Serial(SERIAL_PORT, SERIAL_RATE, timeout=1)
            print 'Serial Connections created!'
        except Exception as e:
            print e
            print 'Failed connecting to motorboards, check serial port address.'
            self.ser = None
            
        try:
            self.arduino = serial.Serial(SERIAL_PORT2, SERIAL_RATE, timeout=1)
            self.arduino.write('C')
            print 'Arduino Serial Connection created!'
        except Exception as e:
            print e
            print 'Failed connecting to Arduino.'
            self.arduino = None
    
    def run(self):
        print 'Initiating Motor Control'
        if self.ser is None:
            self._make_serial_connection()
        
        self.robot.run_control = True
        while self.robot.run_control:
            # check for kill all signal
            if self.robot.kill_all:
                self._all_motors_off()
                self.robot.kill_all = False
                
            # update everything
            self._update_arm()
            self._update_bucket()
            self._update_motors()

    def _send_command(self,command):
        self.ser.write(command)
        print command
        time.sleep(self.robot.update_rate)
                
    def _update_arm(self):
        if self.ser is not None:
            if self.robot.arm_current < self.robot.arm_target:
                self.robot.arm_current += 1
                self._send_command(self.robot.arm_states['up'])
                if self.robot.arm_current == 0:
                    self._send_command(self.robot.arm_states['stop'])
            elif self.robot.arm_current > self.robot.arm_target:
                self.robot.arm_current -= 1
                self._send_command(self.robot.arm_states['down'])
                if self.robot.arm_current == 0:
                    self._send_command(self.robot.arm_states['stop'])
        
    def _update_bucket(self):
        if self.ser is not None:
            if self.robot.bucket_current < self.robot.bucket_target:
                self.robot.bucket_current += 1
                self._send_command(self.robot.bucket_states['up'])
                if self.robot.bucket_current == 0:
                    self._send_command(self.robot.bucket_states['stop'])
            elif self.robot.bucket_current > self.robot.bucket_target:
                self.robot.bucket_current -= 1
                self._send_command(self.robot.bucket_states['down'])
                if self.robot.bucket_current == 0:
                    self._send_command(self.robot.bucket_states['stop'])
                
    def _update_motors(self):
        if self.ser is not None:
            if self.robot.forward_state < self.robot.forward_target:
                print 'state:',self.robot.forward_state,'\target:',self.robot.forward_target
                self._send_command(self.robot.robot_states['forward'])
                self.robot.forward_state += 1
                if self.robot.forward_state == 0:
                    self._send_command(' ')
            elif self.robot.forward_state > self.robot.forward_target:
                self._send_command(self.robot.robot_states['reverse'])
                self.robot.forward_state -= 1
                if self.robot.forward_state == 0:
                    self._send_command(' ')
            
            #if abs(self.robot.forward_target-self.robot.forward_state) <= self.robot.TURN_SPEED:
            if self.robot.turn_state < self.robot.turn_target:
                self._send_command(self.robot.robot_states['left'])
                self.robot.turn_state += 1
            elif self.robot.turn_state > self.robot.turn_target:
                self._send_command(self.robot.robot_states['right'])
                self.robot.turn_state -= 1
                    
    def reset_mocobo(self):
        if self.arduino is not None:
            self.arduino.write('R')
            time.sleep(0.060)
        
                
    def _all_motors_off(self):
        print 'kill all'
        if self.ser is not None:
            self._send_command(' ')
            
            
