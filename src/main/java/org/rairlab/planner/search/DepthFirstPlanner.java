package org.rairlab.planner.search;

import org.rairlab.planner.utils.Commons;
import org.rairlab.planner.utils.PlanningProblem;
import org.rairlab.planner.utils.Visualizer;
import org.rairlab.planner.*;

import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.shadow.prover.utils.Sets;

import org.apache.commons.lang3.tuple.Pair;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class DepthFirstPlanner implements Planner {


    private static  int MAX_DEPTH = 7;
    private static  boolean EXHAUSTIVE_TILL_MAX_DEPTH = true;

    private  boolean USE_METHODS, WORK_FROM_SCRATCH;

    public DepthFirstPlanner(){
        USE_METHODS = true;
        WORK_FROM_SCRATCH = true;

    }

    public boolean isWORK_FROM_SCRATCH() {
        return WORK_FROM_SCRATCH;
    }

    public void setWORK_FROM_SCRATCH(boolean WORK_FROM_SCRATCH) {
        this.WORK_FROM_SCRATCH = WORK_FROM_SCRATCH;
    }

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

    @Override
    public Optional<Set<Plan>> plan(PlanningProblem problem, Set<Formula> background, Set<Action> actions, State start, State goal) {

        if(USE_METHODS){

            Optional<Set<Plan>> optionalPlansFromMethods = plan(problem, background, actions, start, goal, problem.getPlanMethods());

            if(optionalPlansFromMethods.isPresent()){
                return optionalPlansFromMethods;
            }
        }

        if(!WORK_FROM_SCRATCH){
            return Optional.empty();
        }


        if (!EXHAUSTIVE_TILL_MAX_DEPTH) {

            return planInternal(Sets.newSet(), 0, MAX_DEPTH, background, actions, start, goal);

        } else {

            Set<Plan> possiblePlans = Sets.newSet();
            for (int i = 1; i <= MAX_DEPTH; i++) {

                Optional<Set<Plan>> plansOpt = planInternal(Sets.newSet(), 0, i, background, actions, start, goal);

                if (plansOpt.isPresent()) {

                    Set<Plan> complyingPlans = plansOpt.get().stream().
                                                 filter(plan-> plan.getActions().stream().
                                                                map(Action::getShorthand).
                                                                noneMatch(shortHand-> {

                                                                            return problem.getAvoidIfPossible().
                                                                                    stream().map(Object::toString).
                                                                                    collect(Collectors.toSet()).
                                                                                    contains(shortHand.getName());

                                                                            })).
                                                  collect(Collectors.toSet());


                    if(!complyingPlans.isEmpty()){
                        return Optional.of(complyingPlans);
                    }
                    else{

                        possiblePlans.addAll(plansOpt.get());
                    }

                }

            }
//
            if(possiblePlans.isEmpty()){
                return Optional.empty();

            } else{

                return Optional.of(possiblePlans);
            }

        }


    }


     public Optional<Set<Plan>> plan(PlanningProblem problem, Set<Formula> background, Set<Action> actions, State start, State goal, List<PlanMethod> planMethods){

        Set<Plan> plans = Sets.newSet();

        Function<PlanSketch, Optional<Plan>> verifier = (planSketch) -> verify(background, start, goal, planSketch);

        for (PlanMethod planMethod: planMethods){

            Optional<List<PlanSketch>> optionalPlanSketches = planMethod.apply(background, start.getFormulae(), goal.getFormulae(), problem.getActions());

            optionalPlanSketches.ifPresent(planSketches -> planSketches.forEach(planSketch -> verifier.apply(planSketch).ifPresent(plans::add)));

        }


        if(!plans.isEmpty()){

            return Optional.of(plans);

        } else {

            return Optional.empty();

        }




    }


    public Optional<Plan> verify(Set<Formula> background, State start, State goal, PlanSketch planSketch){

        List<Action> sketchActions = planSketch.getActions();
        List<State> expectedStates = CollectionUtils.newEmptyList();

        Set<Formula> current = Sets.union(background, start.getFormulae());

        expectedStates.add(State.initializeWith(current));
        for(Action action: sketchActions){

            Set<Formula> preconditions = action.getPreconditions();
            Set<Formula> additions = action.getAdditions();
            Set<Formula> deletions = action.getDeletions();

            Optional<Justification> optPrecond = Operations.proveCached(current, Commons.makeAnd(preconditions));

            if(optPrecond.isPresent()){


                current.removeAll(current.stream().filter(u-> deletions.stream().anyMatch(d-> Operations.equivalent(background, d, u))).collect(Collectors.toSet()));
                current.removeAll(deletions);
                current.addAll(additions.stream().collect(Collectors.toSet()));

                expectedStates.add(State.initializeWith(current));

            } else {

                return Optional.empty();
            }

        }


        if(!Operations.proveCached(current, Commons.makeAnd(goal.getFormulae())).isPresent()){

            return Optional.empty();
        }

        return Optional.of(new Plan(sketchActions, expectedStates, background));


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

        for (Action action : actions.stream().filter(Action::isNonTrivial).collect(Collectors.toSet())) {

            Optional<Set<Pair<State, Action>>> nextStateActionPairs = Operations.apply(background, action, start);

            if (nextStateActionPairs.isPresent()) {

                for (Pair<State, Action> stateActionPair : nextStateActionPairs.get()) {

                    Visualizer.push();
                    Optional<Set<Plan>> planOpt = planInternal(history, currentDepth + 1, maxDepth, background, actions, stateActionPair.getLeft(), goal);

                    Visualizer.pop();

                    if (planOpt.isPresent()) {

                        atleastOnePlanFound = true;
                        Set<Plan> nextPlans = planOpt.get();

                        State nextSate = stateActionPair.getLeft();
                        Action instantiatedAction = stateActionPair.getRight();

                        Set<Plan> augmentedPlans = nextPlans.stream().
                                map(plan -> plan.getPlanByStartingWith(instantiatedAction, nextSate)).
                                collect(Collectors.toSet());

                        allPlans.addAll(augmentedPlans);

                  //      return Optional.of(allPlans);

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
