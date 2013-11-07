from robot_server import ServerControl
from robot_control import RobotController

class Robot:
    robot_state = 'Q'
    robot_states = {'stop':'Q','forward':'W',
                    'reverse':'S','right':'D','left':'A'}

    motor_currents = [0, 0, 0, 0]
    motor_targets = [0, 0, 0, 0]
    
    forward_state = 0 # pos: forward  | neg: back
    turn_state = 0    # pos: left     | neg: right
    forward_target = 0
    turn_target = 0
    
    FORWARD_SPEED = 30
    BACK_SPEED = -20
    TURN_SPEED = 10

    arm_state = 'N'
    arm_states = {'stop':'N','down':'O','up':'K'}
    arm_current = 0
    arm_target = 0
    
    bucket_state = 'M'
    bucket_states = {'stop':'M','down':'L','up':'P'}
    bucket_current = 0
    bucket_target = 0
    
    run_control = True
    kill_all = True
    
    update_rate = 0.02
##    motor_delta = 1
##    MAX_MOTOR = 45
    
    def __init__(self):
        server = ServerControl(self)
        server.setDaemon(True)
        server.start()
        self.controller = RobotController(self)
        self.controller.run()
    
    def set_arm_state(self, state):
        self.arm_state = state
        if self.arm_state == 'N':
            self.arm_target = 0
        elif self.arm_state == 'K':
            self.arm_target = -1
        elif self.arm_state == 'O':
            self.arm_target = 1
    
    def set_bucket_state(self, state):
        self.bucket_state = state
        if self.bucket_state == 'M':
            self.bucket_target = 0
        elif self.bucket_state == 'L':
            self.bucket_target = -1
        elif self.bucket_state == 'P':
            self.bucket_target = 1
        
    def set_robot_state(self, state):
        if self.robot_state != state:
            self.robot_state = state
            if self.robot_state == 'Q':
                # All motors off
                self.forward_target = 0
                self.turn_target = 0
            elif self.robot_state == 'W':
                # All motors forward
                print 'forw targ:',self.FORWARD_SPEED
                self.forward_target = self.FORWARD_SPEED
                self.turn_target = 0
            elif self.robot_state == 'S':
                # All motors backwards
                self.forward_target = self.BACK_SPEED
                self.turn_target = 0
            elif self.robot_state == 'D':
                # forward right
                self.forward_target = self.FORWARD_SPEED
                self.turn_target = -(self.TURN_SPEED)
            elif self.robot_state == 'A':
                # forward left
                self.forward_target = self.FORWARD_SPEED
                self.turn_target = self.TURN_SPEED
            elif self.robot_state == 'Z':
                # back right
                self.forward_target = self.BACK_SPEED
                self.turn_target = -(self.TURN_SPEED)
            elif self.robot_state == 'C':
                # back left
                self.forward_target = self.BACK_SPEED
                self.turn_target = self.TURN_SPEED
 
    def all_motors_off(self):
        self.robot_state = 'Q'
        self.forward_target = 0
        self.turn_target = 0
        self.arm_state = 'N'
        self.arm_target = 0
        self.bucket_state = 'M'
        self.bucket_target = 0
        self.kill_all = True

        
if __name__=='__main__':
    robot = Robot()
