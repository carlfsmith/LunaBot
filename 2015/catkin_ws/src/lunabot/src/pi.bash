#!/bin/bash

echo "running all nodes..."

#echo "starting controller..."
#rosrun beginner_tutorals control.py
#echo "controller started..."

#echo "starting converter..."
#rosrun beginner_tutorals control_to_robot.py
#echo "converter started..."

echo "starting bin..."
rosrun beginner_tutorals bin.py
echo "bin started..."

echo "starting extractor..."
rosrun beginner_tutorals extractor.py
echo "extractor started..."

echo "starting exctractor rotator..."
rosrun beginner_tutorials extractor.py
echo "exctractor rotator started..."

echo "starting kill switch..."
rosrun beginner_tutorials killSwitch.py
echo "kill switch started..."

echo "all nodes are running..."
