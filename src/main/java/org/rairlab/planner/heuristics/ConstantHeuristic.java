package org.rairlab.planner.heuristics;

import org.rairlab.planner.State;

public class ConstantHeuristic {
    public static int h(State s) {
        return 1;
    }
}
