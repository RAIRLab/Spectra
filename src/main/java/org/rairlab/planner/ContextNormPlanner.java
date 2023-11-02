package org.rairlab.planner;

import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.planner.search.DepthFirstPlanner;

import java.util.Optional;
import java.util.Set;

public class ContextNormPlanner implements Planner {

    private static DepthFirstPlanner depthFirstPlanner = new DepthFirstPlanner();
    @Override
    public Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {
        return depthFirstPlanner.plan(background, actions, start, goal);


    }

    @Override
    public Optional<Set<Plan>> plan(PlanningProblem problem, Set<Formula> background, Set<Action> actions, State start, State goal) {

        Optional<Context> contextOpt = problem.getContextOpt();

        if(contextOpt.isPresent()){


            depthFirstPlanner.setWORK_FROM_SCRATCH(contextOpt.get().isWorkFromScratch());


            return depthFirstPlanner.plan(problem, background, actions, start, goal);


        } else{

            return depthFirstPlanner.plan(problem, background, actions, start, goal);
        }


    }
}
