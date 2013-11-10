//Quad Anti-Phase Lock Motor control
//PIC18F4431 (10 MHz Crystal, PPL 40MHz, 10MIPS)
//Written By Chris Farnell and Brett Sparkman
//30 July 2010
//Last Modified 9 November 2013
//Modified by Lucas Dorrough

#define USE_OR_MASKS 1 		// use '|' for multi variable function calls
#include <p18f4431.h>
#include <pcpwm.h>
#include <adc.h>
#include <timers.h>
#include <delays.h>
#include <stdlib.h>
#include <stdio.h>
#include <usart.h>
#include <math.h>
#include <string.h>
#include <ctype.h>

// Configure Fuses
#pragma config OSC=HSPLL	// High Speed Phase-Lock Loop (40MHz Clk w/ 10MIPS)
#pragma config FCMEN=OFF	// Disable Automatic Clock Switch over during failure
#pragma config BOREN=OFF	// Disable Brownout detection
#pragma config WDTEN=OFF	// Disable WatchDog Timer
#pragma config PWM4MX = RB5	// Set PWM4 Multiplexer to port RB5

//// Define Pinouts
//LED Pins
#define	LEDA PORTDbits.RD2	// Motor A LED
#define LEDB PORTDbits.RD3	// Motor B LED
#define LEDC PORTDbits.RD4	// Motor C LED
#define LEDD PORTDbits.RD5	// Motor D LED
//Switch Pins
// Switch position 0 used for analog mux s0
// Switch position 1 used for analog mux s1
#define SW2 PORTAbits.RA0	// Switch position 2
#define SW3 PORTCbits.RC0	// Switch position 3
#define SW4 PORTCbits.RC1	// Switch position 4
#define SW5 PORTCbits.RC2	// Switch position 5
#define SW6 PORTCbits.RC3	// Switch position 6
#define SW7 PORTDbits.RD0	// Switch position 7 (Used for Master/Slave Mode Select)
//Relay Pins
#define RA PORTCbits.RC4	//Relay for F:LEFT
#define RB PORTCbits.RC5	//Relay for F:RIGHT
#define RC PORTDbits.RD1	//Relay for R:LEFT
#define RD PORTEbits.RE0	//Relay for R:RIGHT 
//#define RE PORTEbits.RE1	//NOT USED
//#define RF PORTEbits.RE2	//NOT USED

//////Load Static Values into ROM
////PWM Constants
rom int PWM_Max=1024;	//max
rom int PWM_Stop=512;	//50% Duty Cycle (x/1024) =%DC
rom float PWM_Conv=10.24;	//max
rom int PWM_Step=8;	//PWM Step Size;
rom int PWM_DC_Min=104;	//Minimum Duty Cycle (x/1024) =%DC... Multiples of 8
rom int PWM_DC_Max=936;	//Maximum Duty Cycle (x/1024) =%DC... Multiples of 8
rom int PWM_Delay=10;	//Delay for steps between PWM updates (Ramp time ms) 

//int Channel_A_ID1=65;
//rom char Channel_B_ID1='B';
//rom char Channel_C_ID1='C';
//rom char Channel_D_ID1='D';
//
//rom char Channel_A_ID2='E';
//rom char Channel_B_ID2='F';
//rom char Channel_C_ID2='G';
//rom char Channel_D_ID2='H';

/*******************************************************************************
 * Program Information:
 * This program uses the PIC18F4431 to control 4 DC Motors via Locked Anti-Phase.
 * Written by Chris Farnell and Brett Sparkman on 30 July 2010.
 * The four Complementary PWMs are as follows:
 * Channel A= Ports B5 and B4, PWM 2 and 2I, invert input
 * Channel B= Ports B2 and B3, PWM 1I and 1
 * Channel C= Ports B0 and B1, PWM 0I and 0
 * Channel D= Ports D6 and D7, PWM 3I and 3
 * Port RD0 is used to select between Master and Slave Mode.
 * The PWM signal is 39kHz with 1us of 'Dead Time' to prevent shoot through current.
 * The Serial Comm Port is Configured for 9600 baud with 8 Data, 1 Stop, No Parity.
 * The Crystal used for this setup is 10MHz and PLL is enabled which gives an
 * FOSC of 40MHz and 10MIPS
 *******************************************************************************/
////Global Variables
//Current PWM Values
int PWMTEMP			// PWM temp variable
int PWMA2;			// PWM Variable for motor A, Setdc2pcpwm()
int PWMB1;			// PWM Variable for motor B, Setdc1pcpwm()
int PWMC0;			// PWM Variable for motor C, Setdc0pcpwm()
int PWMD3;			// PWM Variable for motor D, Setdc3pcpwm()
//Old PWM values
int PWMA2_Old;			// PWM Variable for motor A, Setdc2pcpwm()
int PWMB1_Old;			// PWM Variable for motor B, Setdc1pcpwm()
int PWMC0_Old;			// PWM Variable for motor C, Setdc0pcpwm()
int PWMD3_Old;			// PWM Variable for motor D, Setdc3pcpwm()

char key;			// Char variable for input
char percent[2];	// Char array for percent input

rom char ClrScr[]= "\x0A\x0D";
rom char BadKey[]= "Invalid Key";
rom char Menu1[]= "Welcome to the Anti Phase-Lock Motor Controller Menu Interface\r\n\
Please Select an option below to Continue.\r\n1) About this Program\r\n2) Track Mode\r\n\
3) Manual Mode\r\n4) Blink\r\n\nSelection:";

rom char Menu1_1[]= "*******************************************************************************\r\n\
Program Information:\r\n\
This program uses the PIC18F4431 to control 4 DC Motors via Locked Anti-Phase.\r\n\
Written by Chris Farnell and Brett Sparkman on 30 July 2010.\r\n\
Last Modified on 18 April 2013.\r\n\
The four Complementary PWMs are as follows:\r\n\
Channel A = Ports B5 and B4\r\n\
Channel B = Ports B2 and B3\r\n\
Channel C = Ports B0 and B1\r\n\
Channel D = Ports D6 and D7\r\n\
SW7 is used to select between Master and Slave Mode.\r\n\
The PWM signal is 39kHz with 1us of 'Dead Time' to prevent shoot through current.\r\n\
The Serial Comm Port is Configured for 9600 baud with 8 Data, 1 Stop, No Parity.\r\n\
The Crystal used for this setup is 10MHz and PLL is enabled which gives an \r\n\
FOSC of 40MHz and 10MIPS\r\n\
*******************************************************************************.\r\n";

rom char Menu1_2[]= "Track mode active. In this mode Channels 1/3 and 2/4 are linked.\r\n\
Track Directions:\r\nW= Forward, S= Back, A= Left, D= Right.\r\n\
Space bar is ALL STOP\r\n\nTo Return to Main Menu press ESC.\r\n";

rom char Menu1_3[]= "Manual mode active. In this mode all 4 channels are independent.\r\n\
The command syntax is 'xyy' where x denotes channels A-D and yy is a two digit number between 0-99\r\n\
Examples:\r\n'A99' will set channel A to 100% forward\r\n'A00' will set Channel A to 100% reverse\r\n\
Space bar is ALL STOP and a '-' denotes and invalid selection.\r\n\nTo Return to Main Menu press ESC.\r\n";

rom char Menu1_4[]= "Slave mode active. In this mode all 4 channels are independent.\r\n\
The command syntax is 'xyy' where x denotes channels A-D and yy is a two digit number between 0-99\r\n\
Examples:\r\n'A99' will set channel A to 100% forward\r\n'A00' will set Channel A to 100% reverse\r\n\
Space bar is ALL STOP and a '-' denotes and invalid selection.\r\n";

//Functions
void manmode(void);
void blink(void);
void Update_PWMs(void);

void main(void){
  // PWM Setup
  TRISA = 0xFF;	// RA0-3=ADC3-0 (I), RA4=BLINKY/NC (I), RA5=SW2 (I), RA6-7=OSC (I)
  TRISB = 0x00;	// RB0-5=PWM0-2 (O), RB6-7=ICSP (O)
  TRISC = 0x8F;	// RC0-3=SW3-6 (I), RC4-5=Header (O), RC6=TX (O), RC7=RX (I)
  TRISD = 0x01;	// RD0=SW7 (I), RD1=Header (O), RD2-5=LED1-4 (O), RD6-7=PWM3 (O)
  TRISE = 0x00;	// RE0-RE2 (O)

  // Configure USART for 9600 Baud Rate
  OpenUSART( USART_TX_INT_ON | USART_RX_INT_ON |
    USART_ASYNCH_MODE | USART_EIGHT_BIT |
    USART_CONT_RX | USART_BRGH_LOW, 64 );
  // End USART Setup

  //Setup Complimentary PWMs for 39 kHz
  DTCON = 0;   		// 20 Set dead time between pairs to 1.092us
  Openpcpwm(PWM_IO_ALL | PWM_0AND1_COMPLI |
    PWM_2AND3_COMPLI | PWM_4AND5_COMPLI |
    PWM_6AND7_COMPLI, PW_SEVT_POS_1_16 |
    PW_SEVT_DIR_UP | PW_OP_SYNC, PT_MOD_FREE_RUN,
  PT_ENABLE | PT_CNT_UP, 0X0FF, 0);
  //End PWM Setup

  // Setup ADC for RA0-3 from channels
  OpenADC(ADC_CONV_CONTINUOUS | ADC_MODE_MULTI_CH |
    ADC_CONV_SEQ_SEQM2 | INT_4_WR_BUF | ADC_INT_ON,
  ADC_REF_VDD_VSS | ADC_FIFO_DIS | ADC_TRIG_PCPWM,
  ADC_LEFT_JUST | ADC_32_TAD | ADC_FOSC_2 |
    ADC_CH_GRA_SEL_AN0 | ADC_CH_GRB_SEL_AN1 |
    ADC_CH_GRC_SEL_AN2 | ADC_CH_GRD_SEL_AN3);
  // End ADC Setup

  //Set all PWMs to 50% DC ("0" volts)
  PWMA2=PWM_Stop;
  PWMB1=PWM_Stop;
  PWMC0=PWM_Stop;
  PWMD3=PWM_Stop;
  PWMA2_Old=PWM_Stop;
  PWMB1_Old=PWM_Stop;
  PWMC0_Old=PWM_Stop;
  PWMD3_Old=PWM_Stop;

  Setdc2pcpwm(PWMA2);
  Setdc1pcpwm(PWMB1);
  Setdc0pcpwm(PWMC0);
  Setdc3pcpwm(PWMD3);

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

  Delay10KTCYx(10);		//Short Delay to allow for configuration
  
  //Check switch position 7 and jump to manmode or test mode
  if (SW7==0)
    manmode();	//manmode();
  else
   blink();		//Testing mode

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
    while(1){
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



//Manual Mode
//Assume motor arrangement:
//A:F-Left	
//B:F-Right	
//C:R-Left	
//D:R-Right

//Motor connected shortcuts:
//E =A+B+C+D (ALL)
//F =A+C (Left side)
//G =B+D (Right Side)
//H =A+D (Back diagonal)
//I =B+C (Forward diagonal)

//Bucket Controls:
//NONE at the moment

void manmode(void){
  WriteUSART(27);
  putrsUSART (ClrScr);
  putrsUSART (Menu1_3);

  while(1){
    while(!DataRdyUSART());  //wait for data
    key = ReadUSART();      //read data

    switch (key) {
	//INDIVIDUAL
    case 'A':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMA2=atoi(percent);			//convert the string to an integer
        PWMA2=PWM_Conv*PWMA2;			//convert to appropriate Duty Cycle value
        break;
      }
    case 'B':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMB1= atoi(percent);			//convert the string to an integer
        PWMB1=PWM_Conv*PWMB1;			//convert to appropriate Duty Cycle value
        break;
      }
    case 'C':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMC0= atoi(percent);			//convert the string to an integer
        PWMC0=PWM_Conv*PWMC0;			//convert to appropriate Duty Cycle value
        break;
      }
    case 'D':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMD3= atoi(percent);			//convert the string to an integer
        PWMD3=PWM_Conv*PWMD3;			//convert to appropriate Duty Cycle value
        break;
      }
	//CONNECTED  
	case 'E':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMTEMP=atoi(percent);			//convert the string to an integer
        PWMA2=PWM_Conv*PWMTEMP;
		PWMB1=PWM_Conv*PWMTEMP;		//convert to appropriate Duty Cycle value
		PWMC0=PWM_Conv*PWMTEMP;
		PWMD3=PWM_Conv*PWMTEMP;
        break;
      }
    case 'F':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMTEMP=atoi(percent);			//convert the string to an integer
        PWMA2=PWM_Conv*PWMTEMP;			//convert to appropriate Duty Cycle value
		PWMC0=PWM_Conv*PWMTEMP;	
        break;
      }
    case 'G':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMTEMP=atoi(percent);			//convert the string to an integer
		PWMB1=PWM_Conv*PWMTEMP;			//convert to appropriate Duty Cycle value
		PWMD3=PWM_Conv*PWMTEMP;			
        break;
      }
    case 'H':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMTEMP=atoi(percent);			//convert the string to an integer
        PWMA2=PWM_Conv*PWMTEMP;			//convert to appropriate Duty Cycle value
		PWMD3=PWM_Conv*PWMTEMP;			
        break;
      }
	case 'I':
      {
        while(!DataRdyUSART());  		//wait for data
        percent[0] = ReadUSART();      	//read data

        while(!DataRdyUSART());  		//wait for data
        percent[1] = ReadUSART();      	//read data

        PWMTEMP=atoi(percent);			//convert the string to an integer
		PWMB1=PWM_Conv*PWMTEMP;			//convert to appropriate Duty Cycle value
		PWMC0=PWM_Conv*PWMTEMP;
        break;
      }
	
	//BUCKET
	
	  
	//E-BRAKE
    case ' ':
      {
        PWMA2=PWM_Stop;
        PWMB1=PWM_Stop;
        PWMC0=PWM_Stop;
        PWMD3=PWM_Stop;
		RA = 0;
		RB = 0;
		RC = 0;
		RD = 0;
		
        break;
      }
    case 27:
      {
        return;
        break;
      }
    default:
      {
        putrsUSART (BadKey);
        break;
      }
    }

	//Upate the PWM signals
	Update_PWMs();
  }
  return;
}


//Update PWMs with ramping based on delay
void Update_PWMs(void){

	////Round Command Values to multiples of PWM_Step
	//Channel_A
	PWMA2=PWMA2/PWM_Step;
	PWMA2=PWMA2*PWM_Step;
	//Channel_B
	PWMB1=PWMB1/PWM_Step;
	PWMB1=PWMB1*PWM_Step;
	//Channel_C
	PWMC0=PWMC0/PWM_Step;
	PWMC0=PWMC0*PWM_Step;
	//Channel_D
	PWMD3=PWMD3/PWM_Step;
	PWMD3=PWMD3*PWM_Step;

	////Ensure Min and Max Duty Cycles are not exceeded
	//Channel_A
	if(PWMA2>PWM_DC_Max)
		PWMA2=PWM_DC_Max;
	if else(PWMA2<PWM_DC_Min)
		PWMA2=PWM_DC_Min;
	//Channel_B
	if(PWMB1>PWM_DC_Max)
		PWMB1=PWM_DC_Max;
	if else(PWMB1<PWM_DC_Min)
		PWMB1=PWM_DC_Min;
	//Channel_C
	if(PWMC0>PWM_DC_Max)
		PWMC0=PWM_DC_Max;
	if else(PWMC0<PWM_DC_Min)
		PWMC0=PWM_DC_Min;
	//Channel_D
	if(PWMD3>PWM_DC_Max)
		PWMD3=PWM_DC_Max;
	if else(PWMD3<PWM_DC_Min)
		PWMD3=PWM_DC_Min;


	////Update all PWMs at the same time. one step per loop. PWM_Delay inbetween loops
	while(PWMA2!=PWMA2_Old || PWMB1!=PWMB1_Old || PWMC0!=PWMC0_Old || PWMD3!=PWMD3_Old)
	{
	//Channel_A
		if(PWMA2>PWMA2_Old)
			PWMA2_Old=PWMA2_Old+PWM_Step;
	
		if else(PWMA2<PWMA2_Old)
			PWMA2_Old=PWMA2_Old-PWM_Step;
		
		if(PWMA2_Old<PWM_Stop)
		{
			RA=1;
			Setdc2pcpwm(PWM_Max-PWMA2_Old);
		}
		else
		{
			RA=0;
			Setdc2pcpwm(PWMA2_Old);
		}		
			
	//Channel_B
		if(PWMB1>PWMB1_Old)
			PWMB1_Old=PWMB1_Old+PWM_Step;
					

		if else(PWMB1<PWMB1_Old)
			PWMB1_Old=PWMB1_Old-PWM_Step;
		
		if(PWMB1_Old<PWM_Stop)
		{
			RB=1;
			Setdc1pcpwm(PWM_Max-PWMB1_Old);
		}
		else
		{
			RB=0;
			Setdc1pcpwm(PWMB1_Old);
		}	
		
	//Channel_C
		if(PWMC0>PWMC0_Old)
			PWMC0_Old=PWMC0_Old+PWM_Step;
					
		if else(PWMC0<PWMC0_Old)
			PWMC0_Old=PWMC0_Old-PWM_Step;
			
		if(PWMC0_Old<PWM_Stop)
		{
			RC=1;
			Setdc0pcpwm(PWM_Max-PWMC0_Old);
		}
		else
		{
			RC=0;
			Setdc0pcpwm(PWMC0_Old);
		}	
			
	//Channel_D
		if(PWMD3>PWMD3_Old)
			PWMD3_Old=PWMD3_Old+PWM_Step;
		
		if else(PWMD3<PWMD3_Old)
			PWMD3_Old=PWMD3_Old-PWM_Step;
			
		if(PWMD3_Old<PWM_Stop)
		{
			RD=1;
			Setdc3pcpwm(PWM_Max-PWMD3_Old);
		}
		else
		{
			RD=0;
			Setdc3pcpwm(PWMD3_Old);
		}
		
		Delay10KTCYx(PWM_Delay);
	}
  return;
}
