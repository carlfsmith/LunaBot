/*
 *	Author: Alex Anderson
 *	Purpose:Calculate the ratio of speeds between the left and
 *				right sides of a skid-steer (tank) drive train.
 *	Note:	Equilibrium point is calculated automatically based on the range
 *			Ratios range = [-1, 1]
 *			Angles work in degrees or radians so long as the use is consistent
 *	Date:	2/14/14
 */

#ifndef ANDGLEDRIVE_H
#define ANGLEDRIVE_H

class AngleDrive
{
public:
	AngleDrive(float range[2], float deadBand=0);

	void calcRatios(float heading);
	void setRange(float range[2], float deadBand=0);

	float getRightRatio() const;
	float getLeftRatio() const;

	void print() const;

private:

	float rightRatio;
	float leftRatio;

	float range[2];
	float midRange;
	float deadBand;
};

#endif // ANGLEDRIVE_H
