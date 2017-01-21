package edu.rpi.rair;

import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Pair;
import com.naveensundarg.shadow.prover.utils.Sets;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class DepthFirstPlanner implements Planner {


    private static  int MAX_DEPTH = 4;
    private static  boolean EXHAUSTIVE_TILL_MAX_DEPTH = true;

    public static int getMaxDepth() {
        return MAX_DEPTH;
    }

    public static boolean isExhaustiveTillMaxDepth() {
        return EXHAUSTIVE_TILL_MAX_DEPTH;
    }

    public static void setMaxDepth(int maxDepth) {
        MAX_DEPTH = maxDepth;
    }

    public static void setExhaustiveTillMaxDepth(boolean exhaustiveTillMaxDepth) {
        EXHAUSTIVE_TILL_MAX_DEPTH = exhaustiveTillMaxDepth;
    }

    @Override
    public Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {


       if (!EXHAUSTIVE_TILL_MAX_DEPTH) {

            return planInternal(Sets.newSet(), 0, MAX_DEPTH, background, actions, start, goal);

       } else {

            for (int i = 1; i <= MAX_DEPTH; i++) {

                Optional<Set<Plan>> plans = planInternal(Sets.newSet(), 0, i, background, actions, start, goal);

                if (plans.isPresent()) {
                    return plans;
                }

            }
//
          return Optional.empty();

       }


    }


    private Optional<Set<Plan>> planInternal(Set<Pair<State, Action>> history, int currentDepth, int maxDepth, Set<Formula> background, Set<Action> actions, State start, State goal) {

        if (currentDepth >= maxDepth) {
            return Optional.empty();
        }

        if (Operations.satisfies(background, start, goal)) {
            //Already satisfied. Do nothing. Return a set with an empty plan.
            return Optional.of(Sets.with(Plan.newEmptyPlan(start, background)));
        }


        Set<Plan> allPlans = Sets.newSet();
        boolean atleastOnePlanFound = false;

        for (Action action : actions) {

            Optional<Set<Pair<State, Action>>> nextStateActionPairs = Operations.apply(background, action, start);

            if (nextStateActionPairs.isPresent()) {

                for (Pair<State, Action> stateActionPair : nextStateActionPairs.get()) {


                    Optional<Set<Plan>> planOpt = planInternal(history, currentDepth + 1, maxDepth, background, actions, stateActionPair.first(), goal);

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
