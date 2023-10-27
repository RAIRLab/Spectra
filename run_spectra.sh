#!/bin/sh

set -o errexit
set -o nounset

show_usage() {
    echo "Usage: ./run_spectra.sh [FILENAME]"
    exit 1
}

# Check argument count
if [ "$#" -ne 1 ]; then
    show_usage
fi

if ! command -v mvn > /dev/null; then
    echo "Maven (mvn) is not found in the path"
    exit 1
fi


mvn -q exec:java -Dexec.mainClass="org.rairlab.planner.utils.Runner" -Dexec.args="$1"

#mvn exec:java -Dexec.mainClass="org.rairlab.planner.utils.Runner" -Dexec.args="$PWD/src/main/resources/com/naveensundarg/planner/problems/ai2thor/FloorPlan28.clj"

