package com.naveensundarg.planner;

import com.naveensundarg.planner.utils.PlanningProblem;
import com.naveensundarg.shadow.prover.representations.formula.Formula;

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
