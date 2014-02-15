/*
 * Author: Alex Anderson
 * Date: 2/14/14
 */

#include "AngleDrive.h"

#include <math.h>
#include <iostream>

using namespace std;

AngleDrive::AngleDrive(float range[2], float deadBand)
{
	rightRatio = 1.0;
	leftRatio = 1.0;

	this->setRange(range, deadBand);
}

//Calculates and stores ratios based on the heading's offset in the range
void AngleDrive::calcRatios(float heading)
{
	float ang_error = fabs(midRange-heading);	//calculate the error

	if(ang_error < deadBand)	//go straight if we are within the dead-band
	{
		rightRatio = 1.0;
		leftRatio = 1.0;
		return;
	}

	//the range affects the domain, big domain = gentle response
	//Therefore, smaller the range, the snapper the response
	if(midRange > heading)	//turn right
	{
		//leftRatio should be 1, rightRatio should be < 1
		float domain = fabs(midRange - range[0]);	//determines domain bases on the width of the range
		float ratio = 1 - 2 * (ang_error / domain);	//ratio determined by percent of domain

		if(ratio < -1)	//enforce a floor
			ratio = -1;

		leftRatio = 1.0;
		rightRatio = ratio;
	}
	else	//turn left
	{
		//leftRatio should be < 1, rightRatio should be 1
		float domain = fabs(midRange - range[1]);	//determines domain bases on the width of the range
		float ratio = 1 - 2 * (ang_error / domain);	//ratio determined by percent of domain

		if(ratio < -1)	//enforce a floor
			ratio = -1;

		leftRatio = ratio;
		rightRatio = 1.0;
	}
}

//Resets the range, midRange, and deadBand based on the new values
//calcRatios() must be called again to update the ratios
void AngleDrive::setRange(float range[2], float deadBand)
{
	float range_len = range[1] - range[0];

	this->range[0] = range[0];
	this->range[1] = range[1];

	this->midRange = range[1] - (range_len/2.0);

	this->deadBand = deadBand;
}

float AngleDrive::getRightRatio() const
{
	return rightRatio;
}
float AngleDrive::getLeftRatio() const
{
	return leftRatio;
}

void AngleDrive::print() const
{
	cout << "Middle of Range=" << midRange << ", ";
	cout << "Dead band=" << deadBand << ", ";
	cout << "Left Ratio=" << leftRatio << ", ";
	cout << "Right Ratio=" << rightRatio << endl;
}
