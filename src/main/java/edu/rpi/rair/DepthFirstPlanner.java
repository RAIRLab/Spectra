package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Pair;
import com.naveensundarg.shadow.prover.utils.Sets;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class DepthFirstPlanner implements Planner {


    @Override
    public Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {


        if (Operations.satisfies(background, start, goal)) {
            //Already satisfied. Do nothing. Return a set with an empty plan.
            return Optional.of(Sets.with(Plan.newEmptyPlan(goal, background)));
        }


        Set<Plan> allPlans = Sets.newSet();
        boolean atleastOnePlanFound = false;

        for (Action action : actions) {

            Optional<Pair<State, Action>> nextStateActionPair = Operations.apply(background, action, start);

            if (nextStateActionPair.isPresent()) {

                Optional<Set<Plan>> planOpt = plan(background, actions, nextStateActionPair.get().first(), goal);

                if (planOpt.isPresent()) {

                    atleastOnePlanFound = true;
                    Set<Plan> nextPlans = planOpt.get();

                    State nextSate  = nextStateActionPair.get().first();
                    Action instantiatedAction = nextStateActionPair.get().second();

                    Set<Plan> augmentedPlans = nextPlans.stream().
                            map(plan -> plan.getPlanByStartingWith(instantiatedAction, nextSate)).
                            collect(Collectors.toSet());

                    allPlans.addAll(augmentedPlans);
                    //TODO: store different plans and return the best plan.
                }
            }

        }

        if (atleastOnePlanFound) {

            return Optional.of(allPlans);

        } else {

            //No plan found.
            return Optional.empty();

        }


    }
}
