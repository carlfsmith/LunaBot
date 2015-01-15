// this is where we will put our data
double leftInput = 0;
double rightInput = 0;
int leftPin = 0;
int rightPin = 0;
double setpointRight = 0;
double setpointLeft = 0;
double kp = 1, ki = 1, kd = 1;
//byte space = 0;
//byte separator = 0;

void setup(){
  // Start up our serial port, we configured our XBEE devices for 38400 bps. 
  Serial.begin(9200);
  
}

void loop(){
  // handle serial data, if any
  if (Serial.available() >= 4){
    left = Serial.read();
    separator = Serial.read();
    right = Serial.read();
    space = Serial.read();
    Serial.flush();

    Serial.print(left);
    Serial.print(byte(separator));
    Serial.print(right);
    Serial.print(byte(space));
    Serial.print("\n");
  }
}
