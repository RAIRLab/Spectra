#!/bin/sh

# TODO: Argument check and show usage

mvn -q exec:java -Dexec.mainClass="com.naveensundarg.planner.utils.Runner" -Dexec.args="$1"

#mvn exec:java -Dexec.mainClass="com.naveensundarg.planner.utils.Runner" -Dexec.args="$PWD/src/main/resources/com/naveensundarg/planner/problems/ai2thor/FloorPlan28.clj"

