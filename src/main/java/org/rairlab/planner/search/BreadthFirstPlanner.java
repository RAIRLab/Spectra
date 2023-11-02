package org.rairlab.planner.search;

import org.rairlab.shadow.prover.representations.formula.Formula;

import org.rairlab.planner.Action;
import org.rairlab.planner.State;
import org.rairlab.planner.Plan;


import java.util.*;


/**
 * Created by brandonrozek on 03/29/2023.
 */
public class BreadthFirstPlanner {

    private AStarPlanner planner;

    public BreadthFirstPlanner(){
        planner = new AStarPlanner();
    }

    public static int h(State s) {
        return 1;
    }

    public Set<Plan> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {

        // For BFS, need to ignore action costs
        Set<Action> newActions = new HashSet<Action>();
        for (Action a : actions) {
            newActions.add(new Action(
                a.getName(), a.getPreconditions(), a.getAdditions(), a.getDeletions(),
                1, a.openVars(), a.getInterestedVars()
            ));
        }

        return planner.plan(background, actions, start, goal, BreadthFirstPlanner::h);
    }

    public Optional<Integer> getMaxDepth() {
        return planner.getMaxDepth();
    }

    public void setMaxDepth(int maxDepth) {
        planner.setMaxDepth(maxDepth);
    }

    public void setK(int k) {
        planner.setK(k);
    }

    public void clearK() {
        planner.clearK();
    }

    public Optional<Integer> getK() {
        return planner.getK();
    }

}