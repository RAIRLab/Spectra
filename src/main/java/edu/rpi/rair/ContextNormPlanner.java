package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import edu.rpi.rair.utils.PlanningProblem;

import java.util.List;
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
        List<Plan> methods =  problem.getPlanMethods();


    }
}
