/*
 * File:   MoCoBoV10.c
 * Created on November 16, 2013, 5:34 AM
 */

#define USE_OR_MASKS 1 		// use '|' for multi variable function calls
#include <xc.h>
#include <plib/pcpwm.h>
#include <plib/adc.h>
#include <plib/timers.h>
#include <plib/delays.h>
#include <stdlib.h>
#include <stdio.h>
#include <plib/usart.h>
#include <string.h>

//Fuse Configuration settings
//These setting must remain the same to successfully program p18f4431 on the
//MoCoBo. If incorrect the program can be loaded, but MoCoBo will be unresponsive.
//Config 300000 bits<15:8> Hex Val: 8600
#pragma config OSC = HSPLL	// High Speed Phase-Lock Loop (40MHz Clk w/ 10MIPS)
#pragma config IESO = 1
#pragma config FCMEN = OFF	// Disable Automatic Clock Switch over during failure

//Config 300002 bits<15:8>  Hex Val: 3E0D
#pragma config WINEN = 1        // Setting of 1 disables WDT Window
#pragma config WDTEN=OFF	// Disable WatchDog Timer
//Config 300002 bits<7:0>
#pragma config BOREN = OFF	// Disable Brownout detection
#pragma config PWRTEN = 1

//Config 300004 bits<15:8>  Hex Val: 9D3C
#pragma config MCLRE = 1        // MCLR pin enabled
#pragma config EXCLKMX = 1      // TMR0 ext clock mux'ed with RC3
#pragma config PWM4MX = 1	// Set PWM4 Multiplexer to port RB5
#pragma config SSPMX = 1
#pragma config FLTAMX = 1       // FLTA input mux'ed with RC1
//Config 300004 bits<7:0>
#pragma config T1OSCMX = 1
#pragma config HPOL = 1
#pragma config LPOL = 1
#pragma config PWMPIN = 1

//Config 300006 bits<7:0>  Hex Val: 0081
#pragma config DEBUG = 1
#pragma config LVP = 0
#pragma config STVREN = 1

//Config 300008 bits<15:8>  Hex Val: C007
#pragma config CPD = 1;
#pragma config CPB = 1;
//Config 300008 bits<7:0>
#pragma config CP3 = 1
#pragma config CP2 = 1
#pragma config CP1 = 1
#pragma config CP0 = 1

//Config 30000A bits<15:8> Hex Val: E00F
#pragma config WRTD = 1
#pragma config WRTB = 1
#pragma config WRTC = 1
//Config 30000A bits<7:0>
#pragma config WRT3 = 1
#pragma config WRT2 = 1
#pragma config WRT1 = 1
#pragma config WRT0 = 1

//Config 30000C bits<15:8> Hex Val: 400F
#pragma config EBTRB = 1
//Config 30000C bits<7:0>
#pragma config EBTR3 = 1
#pragma config EBTR2 = 1
#pragma config EBTR1 = 1
#pragma config EBTR0 = 1

// Define Pinouts
//Motor Channel Status LED Pins
#define	LEDA PORTDbits.RD2	// Motor A LED
#define LEDB PORTDbits.RD3	// Motor B LED
#define LEDC PORTDbits.RD4	// Motor C LED
#define LEDD PORTDbits.RD5	// Motor D LED

//Switch Pins
// Switch position 0 used for analog mux s0
// Switch position 1 used for analog mux s1
#define SW2 PORTAbits.RA0	// Switch 2
#define SW3 PORTCbits.RC0	// Switch 3
#define SW4 PORTCbits.RC1	// Switch 4
#define SW5 PORTCbits.RC2	// Switch 5
#define SW6 PORTCbits.RC3	// Switch 6
#define SW7 PORTDbits.RD0	// Switch 7 (Used for Drive/Blink Mode Select)

//Relay Pins
//Relays currently used to provide reverse direction for motors.
#define RA PORTEbits.RE0	//Relay for Front Left Motor & Arm Reverse
#define RB PORTEbits.RE2	//Relay for Front Right Motor & Bucket Reverse
#define RC PORTCbits.RC4	//Relay for Back Left Motor
#define RD PORTCbits.RC5	//Relay for Back Right Motor
#define RE PORTEbits.RE1	//Relay for Bucket Power
#define RF PORTDbits.RD1	//Relay for Arm Power

//ROM values for PWM Constants
const int PWM_Max = 1024;	//max
const int PWM_Stop = 512;	//50% Duty Cycle (x/1024) =%DC
const float PWM_Conv = 10.24;	//max
const int PWM_Step = 32;	//PWM Step Size;
const int PWM_DC_Min = 104;	//Minimum Duty Cycle (x/1024) =%DC... Multiples of 8
const int PWM_DC_Max = 936;	//Maximum Duty Cycle (x/1024) =%DC... Multiples of 8
const int PWM_Delay = 2;	//Delay for steps between PWM updates (Ramp time ms)

//Current PWM Values
int PWM_MotorA;			// PWM Variable for motor A, Setdc0pcpwm()
int PWM_MotorB;			// PWM Variable for motor B, Setdc3pcpwm()
int PWM_MotorC;			// PWM Variable for motor C, Setdc2pcpwm()
int PWM_MotorD;			// PWM Variable for motor D, Setdc1pcpwm()

//PWM change values
int Delta_A;			// PWM Variable for motor A, Setdc0pcpwm()
int Delta_B;			// PWM Variable for motor B, Setdc3pcpwm()
int Delta_C;			// PWM Variable for motor C, Setdc2pcpwm()
int Delta_D;			// PWM Variable for motor D, Setdc1pcpwm()

//char variables used to configure PWM, USART, and ADC settings
unsigned char key = 0, usart_config = 0, baud_config = 0;
unsigned char adc_config0 = 0, adc_config1 = 0, adc_config2 = 0;
unsigned char pcpwm_config0 = 0, pcpwm_config1 = 0, pcpwm_config2 = 0, pcpwm_config3 = 0;


const char ClrScr[]= "\x0A\x0D";
const char BadKey[]= "Invalid Key";
const char Menu1_2[]= "Track mode active. In this mode Channels 1/3 and 2/4 are linked.\r\n\
Track Directions:\r\nW= Forward, S= Back, A= Left, D= Right.\r\n\
Space bar is ALL STOP\r\n\nTo Return to Main Menu press ESC.\r\n";

//Function prototypes. Implemenations are after located main()
void PWM_Round(int *motor_val);
void motorInitialization();
void MotorA_PWM_Set(int *motor_val, int delta_val);
void MotorB_PWM_Set(int *motor_val, int delta_val);
void MotorC_PWM_Set(int *motor_val, int delta_val);
void MotorD_PWM_Set(int *motor_val, int delta_val);
void blink(void);
void forward(void);
void back(void);
void left(void);
void right(void);
void strafeLeft(void);
void strafeRight(void);
void forward45left(void);
void forward45right(void);
void backward45left(void);
void backward45right(void);
void armStop(void);
void armDown(void);
void armUp(void);
void bucketStop(void);
void bucketDown(void);
void bucketUp(void);
void driveMode(void);

void main(void) {

  //Tristate Buffer settings for I/O pins
  TRISA = 0xFF;	// RA0-3=ADC3-0 (I), RA4=BLINKY/NC (I), RA5=SW2 (I), RA6-7=OSC (I)
  TRISB = 0x00;	// RB0-5=PWM0-2 (O), RB6-7=ICSP (O)
  TRISC = 0x8F;	// RC0-3=SW3-6 (I), RC4-5=Header (O), RC6=TX (O), RC7=RX (I)
  TRISD = 0x01;	// RD0=SW7 (I), RD1=Header (O), RD2-5=LED1-4 (O), RD6-7=PWM3 (O)
  TRISE = 0x00;	// RE0-RE2 (O)

  // Configure USART for 9600 Baud Rate
  CloseUSART();

  usart_config = USART_TX_INT_ON | USART_RX_INT_ON | USART_ASYNCH_MODE | USART_EIGHT_BIT | USART_CONT_RX | USART_BRGH_LOW;
  OpenUSART( usart_config, 64 );

  baud_config = BAUD_8_BIT_RATE | BAUD_AUTO_OFF;
  baudUSART(baud_config);

  //Setup Complimentary PWMs for 39 kHz
  pcpwm_config0 = PWM_IO_ALL | PWM_0AND1_COMPLI | PWM_2AND3_COMPLI | PWM_4AND5_COMPLI | PWM_6AND7_COMPLI;
  pcpwm_config1 = PW_SEVT_POS_1_16 | PW_SEVT_DIR_UP | PW_OP_SYNC;
  pcpwm_config2 = PT_MOD_FREE_RUN;
  pcpwm_config3 = PT_ENABLE | PT_CNT_UP;

  DTCON = 20;   		// 20 Set dead time between pairs to 1.092us
  Openpcpwm(pcpwm_config0, pcpwm_config1, pcpwm_config2, pcpwm_config3, 0X0FF, 0);

  // Setup ADC for RA0-3 from channels
  adc_config0 = ADC_CONV_CONTINUOUS | ADC_MODE_MULTI_CH | ADC_CONV_SEQ_SEQM2 | INT_4_WR_BUF | ADC_INT_ON;
  adc_config1 = ADC_REF_VDD_VSS | ADC_FIFO_DIS | ADC_TRIG_PCPWM;
  adc_config2 = ADC_LEFT_JUST | ADC_32_TAD | ADC_FOSC_2 | ADC_CH_GRA_SEL_AN0 | ADC_CH_GRB_SEL_AN1 | ADC_CH_GRC_SEL_AN2 | ADC_CH_GRD_SEL_AN3;

  OpenADC(adc_config0,adc_config1,adc_config2);

  //Function to initialize motors to stop position upon startup.
  //MUST BE FIRST FUNCTION AFTER USART, PWM, & ADC CONFIGUATION!
  //FAILING TO DO THIS WILL CAUSE THE MOTORS TO RUN AUTOMACTICLLY UPON STARTUP!
  motorInitialization();

  while(1 == 1) {
      //SW7 allows switching between blink and drive modes.
      if (SW7==0)
          driveMode();
      else
          blink();
  }

  return;
}

void PWM_Round(int *motor_val) {
    *motor_val = *motor_val/ PWM_Step;
    *motor_val = *motor_val * PWM_Step;

    if(*motor_val > PWM_DC_Max)
        *motor_val = PWM_DC_Max;
    if(*motor_val < PWM_DC_Min)
        *motor_val = PWM_DC_Min;

    return;
}

void motorInitialization() {
  //Set all PWMs to 50% DC ("0" volts)
  PWM_MotorA = PWM_Stop;
  PWM_MotorB = PWM_Stop;
  PWM_MotorC = PWM_Stop;
  PWM_MotorD = PWM_Stop;

  Delta_A = PWM_Stop;
  Delta_B = PWM_Stop;
  Delta_C = PWM_Stop;
  Delta_D = PWM_Stop;

  LEDA = 0;
  LEDB = 0;
  LEDC = 0;
  LEDD = 0;
  RA=0;
  RB=0;
  RC=0;
  RD=0;
  RE=0;
  RF=0;

  Setdc3pcpwm(PWM_Stop);
  Setdc2pcpwm(PWM_Stop);
  Setdc1pcpwm(PWM_Stop);
  Setdc0pcpwm(PWM_Stop);

  Delay10KTCYx(10);		//Short Delay to allow for configuration

  return;
}

void MotorA_PWM_Set(int *motor_val, int delta_val){
     do {
        if(*motor_val < delta_val)
            *motor_val = *motor_val + PWM_Step;
        if(*motor_val > delta_val)
            *motor_val = *motor_val - PWM_Step;

    if(*motor_val < PWM_Stop) {
        RA = 1;
        Setdc0pcpwm(PWM_Max - *motor_val);
    }
    else {
        RA = 0;
        Setdc0pcpwm(*motor_val);
    }

        //Delay10KTCYx(PWM_Delay);
    }while(*motor_val != delta_val);

    return;
}

void MotorB_PWM_Set(int *motor_val, int delta_val){
    do {
        if(*motor_val < delta_val)
            *motor_val = *motor_val + PWM_Step;
        if(*motor_val > delta_val)
            *motor_val = *motor_val - PWM_Step;


    if(*motor_val < PWM_Stop){
        RB = 1;
        Setdc3pcpwm(PWM_Max - *motor_val);
    }
    else{
        RB = 0;
        Setdc3pcpwm(*motor_val);
    }

        //Delay10KTCYx(PWM_Delay);
    }while(*motor_val != delta_val);

    return;
}

void MotorC_PWM_Set(int *motor_val, int delta_val){
    do {
        if(*motor_val < delta_val)
            *motor_val = *motor_val + PWM_Step;
        if(*motor_val > delta_val)
            *motor_val = *motor_val - PWM_Step;


    if(*motor_val < PWM_Stop){
        RC = 1;
        Setdc2pcpwm(PWM_Max - *motor_val);
    }
    else{
        RC = 0;
        Setdc2pcpwm(*motor_val);
    }

        //Delay10KTCYx(PWM_Delay);
    }while(*motor_val != delta_val);

    return;
}

void MotorD_PWM_Set(int *motor_val, int delta_val){
    do {
        if(*motor_val < delta_val)
            *motor_val = *motor_val + PWM_Step;
        if(*motor_val > delta_val)
            *motor_val = *motor_val - PWM_Step;


    if(*motor_val < PWM_Stop){
        RD = 1;
        Setdc1pcpwm(PWM_Max - *motor_val);
    }
    else{
        RD = 0;
        Setdc1pcpwm(*motor_val);
    }

        //Delay10KTCYx(PWM_Delay);
    }while(*motor_val != delta_val);

    return;
}

// Blink mode for testing
void blink(void){
    LEDA = 0;
    LEDB = 1;
    LEDC = 0;
    LEDD = 1;
    RA =0;
    RB =1;
    RC =0;
    RD =1;
    RE =0;
    RF =1;
    while(SW7 == 1){
        LEDA ^= 1;
        LEDB ^= 1;
        LEDC ^= 1;
        LEDD ^= 1;

	RA  ^= 1;
	RB  ^= 1;
	RC  ^= 1;
	RD  ^= 1;
	RE ^= 1;
	RF  ^= 1;

        Delay10KTCYx(100);
    }
    // End added loop
}

void forward(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA < PWM_Stop)
        Delta_A = PWM_Stop + 128;
    else if(PWM_MotorA + PWM_Step <= PWM_DC_Max) {
        Delta_A = PWM_MotorA + PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Max;
    }

    if(PWM_MotorB == PWM_Stop || PWM_MotorB < PWM_Stop)
        Delta_B = PWM_Stop + 128;
    else if(PWM_MotorB + PWM_Step <= PWM_DC_Max) {
        Delta_B = PWM_MotorB + PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Max;
    }


    PWM_Round(&Delta_A);
    PWM_Round(&Delta_B);
    Delta_C = Delta_A;
    Delta_D = Delta_B;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void back(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA > PWM_Stop)
        Delta_A = PWM_Stop - 128;
    else if(PWM_MotorA - PWM_Step >= PWM_DC_Min) {
        Delta_A = PWM_MotorA - PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Min;
    }

    if(PWM_MotorB == PWM_Stop || PWM_MotorB > PWM_Stop)
        Delta_B = PWM_Stop - 128;
    else if(PWM_MotorB - PWM_Step >= PWM_DC_Min) {
        Delta_B = PWM_MotorB - PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Min;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_B);
    Delta_C = Delta_A;
    Delta_D = Delta_B;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void stop(void){
    Delta_A = PWM_Stop;
    Delta_B = PWM_Stop;
    Delta_C = PWM_Stop;
    Delta_D = PWM_Stop;

    RE = 0;
    RF = 0;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void left(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA > PWM_Stop)
        Delta_A = PWM_Stop - 128;
    else if(PWM_MotorA - PWM_Step >= PWM_DC_Min) {
        Delta_A = PWM_MotorA - PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Min;
    }

    if(PWM_MotorB == PWM_Stop || PWM_MotorB < PWM_Stop)
        Delta_B = PWM_Stop + 128;
    else if(PWM_MotorB + PWM_Step <= PWM_DC_Max) {
        Delta_B = PWM_MotorB + PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Max;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_B);
    Delta_C = Delta_A;
    Delta_D = Delta_B;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void right(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA < PWM_Stop)
        Delta_A = PWM_Stop + 128;
    else if(PWM_MotorA + PWM_Step <= PWM_DC_Max) {
        Delta_A = PWM_MotorA + PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Max;
    }

    if(PWM_MotorB == PWM_Stop || PWM_MotorB > PWM_Stop)
        Delta_B = PWM_Stop - 128;
    else if(PWM_MotorB - PWM_Step >= PWM_DC_Min) {
        Delta_B = PWM_MotorB - PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Min;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_B);
    Delta_C = Delta_A;
    Delta_D = Delta_B;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void strafeLeft(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA > PWM_Stop)
        Delta_A = PWM_Stop - 128;
    else if(PWM_MotorA - PWM_Step >= PWM_DC_Min) {
        Delta_A = PWM_MotorA - PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Min;
    }

    if(PWM_MotorB == PWM_Stop || PWM_MotorB < PWM_Stop)
        Delta_B = PWM_Stop + 128;
    else if(PWM_MotorB + PWM_Step <= PWM_DC_Max) {
        Delta_B = PWM_MotorB + PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Max;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_B);
    Delta_C = Delta_B;
    Delta_D = Delta_A;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void strafeRight(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA < PWM_Stop)
        Delta_A = PWM_Stop + 128;
    else if(PWM_MotorA + PWM_Step <= PWM_DC_Max) {
        Delta_A = PWM_MotorA + PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Max;
    }

    if(PWM_MotorB == PWM_Stop || PWM_MotorB > PWM_Stop)
        Delta_B = PWM_Stop - 128;
    else if(PWM_MotorB - PWM_Step >= PWM_DC_Min) {
        Delta_B = PWM_MotorB - PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Min;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_B);
    Delta_C = Delta_B;
    Delta_D = Delta_A;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void forward45right(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA < PWM_Stop)
        Delta_A = PWM_Stop + 128;
    else if(PWM_MotorA + PWM_Step <= PWM_DC_Max) {
        Delta_A = PWM_MotorA + PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Max;
    }

    if(PWM_MotorD == PWM_Stop || PWM_MotorD < PWM_Stop)
        Delta_D = PWM_Stop + 128;
    else if(PWM_MotorD + PWM_Step <= PWM_DC_Max) {
        Delta_D = PWM_MotorD + PWM_Step;
    }
    else {
        Delta_D = PWM_DC_Max;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_D);
    Delta_C = PWM_Stop;
    Delta_B = PWM_Stop;

    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);

    return;
}

void forward45left(void) {
    if(PWM_MotorB == PWM_Stop || PWM_MotorB < PWM_Stop)
        Delta_B = PWM_Stop + 128;
    else if(PWM_MotorB + PWM_Step <= PWM_DC_Max) {
        Delta_B = PWM_MotorB + PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Max;
    }

    if(PWM_MotorC == PWM_Stop || PWM_MotorC < PWM_Stop)
        Delta_C = PWM_Stop + 128;
    else if(PWM_MotorC + PWM_Step <= PWM_DC_Max) {
        Delta_C = PWM_MotorC + PWM_Step;
    }
    else {
        Delta_C = PWM_DC_Max;
    }

    PWM_Round(&Delta_B);
    PWM_Round(&Delta_C);
    Delta_A = PWM_Stop;
    Delta_D = PWM_Stop;

    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void backward45right(void) {
    if(PWM_MotorA == PWM_Stop || PWM_MotorA > PWM_Stop)
        Delta_A = PWM_Stop - 128;
    else if(PWM_MotorA - PWM_Step >= PWM_DC_Min) {
        Delta_A = PWM_MotorA - PWM_Step;
    }
    else {
        Delta_A = PWM_DC_Min;
    }

    if(PWM_MotorD == PWM_Stop || PWM_MotorD > PWM_Stop)
        Delta_D = PWM_Stop - 128;
    else if(PWM_MotorD - PWM_Step >= PWM_DC_Min) {
        Delta_D = PWM_MotorD - PWM_Step;
    }
    else {
        Delta_D = PWM_DC_Min;
    }

    PWM_Round(&Delta_A);
    PWM_Round(&Delta_D);
    Delta_C = PWM_Stop;
    Delta_B = PWM_Stop;

    if(PWM_MotorC != PWM_Stop)
        MotorC_PWM_Set(&PWM_MotorC, Delta_C);
    MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    if(PWM_MotorB != PWM_Stop)
        MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    MotorD_PWM_Set(&PWM_MotorD, Delta_D);

    return;
}

void backward45left(void) {
    if(PWM_MotorB == PWM_Stop || PWM_MotorB > PWM_Stop)
        Delta_B = PWM_Stop - 128;
    else if(PWM_MotorB - PWM_Step >= PWM_DC_Min) {
        Delta_B = PWM_MotorB - PWM_Step;
    }
    else {
        Delta_B = PWM_DC_Min;
    }

    if(PWM_MotorC == PWM_Stop || PWM_MotorC > PWM_Stop)
        Delta_C = PWM_Stop - 128;
    else if(PWM_MotorC - PWM_Step >= PWM_DC_Min) {
        Delta_C = PWM_MotorC - PWM_Step;
    }
    else {
        Delta_C = PWM_DC_Min;
    }

    PWM_Round(&Delta_B);
    PWM_Round(&Delta_C);
    Delta_A = PWM_Stop;
    Delta_D = PWM_Stop;

    if(PWM_MotorD != PWM_Stop)
        MotorD_PWM_Set(&PWM_MotorD, Delta_D);
    MotorB_PWM_Set(&PWM_MotorB, Delta_B);
    if(PWM_MotorA != PWM_Stop)
        MotorA_PWM_Set(&PWM_MotorA, Delta_A);
    MotorC_PWM_Set(&PWM_MotorC, Delta_C);

    return;
}

void armStop(void){
    if(PWM_MotorC == PWM_Stop && PWM_MotorD == PWM_Stop) {
        RF=0;
        RA=0;
    }
    return;
}

void armDown(void){
    if(PWM_MotorA == PWM_Stop && PWM_MotorB == PWM_Stop) {
        RF=1;
        RA=1;
    }
    return;
}

void armUp(void){
    if(PWM_MotorA == PWM_Stop && PWM_MotorB == PWM_Stop) {
        RF=1;
        RA=0;
    }
    return;
}

void bucketStop(void){
    if(PWM_MotorA == PWM_Stop && PWM_MotorB == PWM_Stop) {
        RE=0;
        RB=0;
    }
    return;
}

void bucketDown(void){
    if(PWM_MotorA == PWM_Stop && PWM_MotorB == PWM_Stop) {
        RE=1;
        RB=1;
    }
    return;
}

void bucketUp(void){
    if(PWM_MotorA == PWM_Stop && PWM_MotorA == PWM_Stop) {
        RE=1;
        RB=0;
    }
    return;
}

void driveMode(void){
  WriteUSART(27);
  putrsUSART(ClrScr);
  putrsUSART(Menu1_2);

  while(SW7 == 0) {
      while(!DataRdyUSART()); //wait for data
    key = ReadUSART();      //read data

      switch (key) {
          case 'W': {
              forward();
              break;
          }

          case 'S': {
              back();
              break;
          }

          case 'A': {
              left();
              break;
          }

          case 'D': {
              right();
              break;
          }

          case 'Q': {
              forward45left();
              break;
          }

          case 'E': {
              forward45right();
              break;
          }

          case 'Z': {
              backward45left();
              break;
          }

          case 'C': {
              backward45right();
              break;
          }

          case 'X': {
              strafeLeft();
              break;
          }

          case 'V': {
              strafeRight();
              break;
          }

          case ' ': {
              stop();
              break;
          }

          case 'N': {
              armStop();
		break;
	  }
          case 'K': {
              armDown();
		break;
	  }

          case 'O': {
              armUp();
		break;
	  }

          case 'M': {
              bucketStop();
		break;
	  }

          case 'L': {
              bucketDown();
		break;
	  }

          case 'P': {
              bucketUp();
		break;
	  }

          default: {
              putrsUSART(BadKey);
              break;
          }
      }
  }

  return;
}