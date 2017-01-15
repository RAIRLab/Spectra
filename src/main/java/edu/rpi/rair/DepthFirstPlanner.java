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


    private static final int MAX_DEPTH = 4;


    @Override
    public Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {


       return planInternal(Sets.newSet(), 0, background, actions, start, goal);


    }



    private Optional<Set<Plan>> planInternal(Set<Pair<State, Action>> history, int currentDepth, Set<Formula> background, Set<Action> actions, State start, State goal) {

        if(currentDepth>=MAX_DEPTH){
            return Optional.empty();
        }

        if (Operations.satisfies(background, start, goal)) {
            //Already satisfied. Do nothing. Return a set with an empty plan.
            return Optional.of(Sets.with(Plan.newEmptyPlan(goal, background)));
        }


        Set<Plan> allPlans = Sets.newSet();
        boolean atleastOnePlanFound = false;

        for (Action action : actions) {

            Optional<Set<Pair<State, Action>>> nextStateActionPairs = Operations.apply(background, action, start);

            if (nextStateActionPairs.isPresent()) {

                for (Pair<State, Action> stateActionPair : nextStateActionPairs.get()) {



                    Optional<Set<Plan>> planOpt = planInternal(history, currentDepth+1, background, actions, stateActionPair.first(), goal);

                    if (planOpt.isPresent()) {

                        atleastOnePlanFound = true;
                        Set<Plan> nextPlans = planOpt.get();

                        State nextSate = stateActionPair.first();
                        Action instantiatedAction = stateActionPair.second();

                        Set<Plan> augmentedPlans = nextPlans.stream().
                                map(plan -> plan.getPlanByStartingWith(instantiatedAction, nextSate)).
                                collect(Collectors.toSet());

                        allPlans.addAll(augmentedPlans);

                        //TODO: store different plans and return the best plan.
                    }
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
