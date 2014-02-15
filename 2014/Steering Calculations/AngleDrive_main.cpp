#include "AngleDrive.h"

#include <iostream>
#include <cmath>

using namespace std;

int main()
{
	float range[2] = {0, 180};
	cout << "Initial range = [0, 180]\n";
	float db = 5;// * (3.14159 / 180);
	AngleDrive pd = AngleDrive(range, db);
	pd.print();
	const int SIZE = 5;
	float ang1[] = {0, 45, 90, 135, 180};
	float ang2[] = {-90, -45, 0, 45, 90};
	cout << "\n";
	for(int i = 0; i < SIZE; i++)
	{
		cout << "Test angle = " << ang1[i] << endl;
		pd.calcRatios(ang1[i]);
		pd.print();
	}

	cout << "\nReseting range to [-90, 90]\n\n";
	range[0] = -90;
	range[1] = 90;
	pd.setRange(range, db);
	
	for(int i = 0; i < SIZE; i++)
	{
		cout << "Test angle = " << ang2[i] << endl;
		pd.calcRatios(ang2[i]);
		pd.print();
	}
	return 0;
}
