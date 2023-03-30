package com.naveensundarg.planner;

import com.naveensundarg.planner.utils.PlanningProblem;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.utils.Pair;
import com.naveensundarg.shadow.prover.utils.Sets;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;



/**
 * Created by naveensundarg on 1/13/17.
 */
public class BreadthFirstPlanner implements Planner {

    private static int MAX_DEPTH = 7;

    public BreadthFirstPlanner(){ }

    public static int getMaxDepth() {
        return MAX_DEPTH;
    }

    public static void setMaxDepth(int maxDepth) {
        MAX_DEPTH = maxDepth;
    }

    @Override
    public Optional<Set<Plan>> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {


        // Search Space Data Structures
        Set<State> history = new HashSet<State>();
        Queue<Triple<List<State>, List<Action>, Integer>> search = new ArrayDeque<Triple<List<State>,List<Action>,Integer>>();

        // Submit Initial State
        search.add(Triple.of(List.of(start), new ArrayList<Action>(), 0));

        // Breadth First Traversal until
        // - Goal Reached
        // - No more actions can be applied
        // - Max depth reached
        while (!search.isEmpty()) {

            Triple<List<State>, List<Action>, Integer> currentSearch = search.remove();
            
            // Return if we're past the depth limit
            int currentDepth = currentSearch.getRight();
            if (currentDepth >= MAX_DEPTH) {
                return Optional.empty();
            }

            List<State> previous_states = currentSearch.getLeft();
            List<Action> previous_actions = currentSearch.getMiddle();

            State lastState = previous_states.get(previous_states.size() - 1);

            // If we're at the goal return
            if (Operations.satisfies(background, lastState, goal)) {
                return Optional.of(Sets.with(
                    new Plan(previous_actions, previous_states, background)
                ));
            }

            // Try to apply each action to get to the next state
            for (Action action : actions.stream().filter(Action::isNonTrivial).collect(Collectors.toSet())) {
                Optional<Set<Pair<State, Action>>> nextStateActionPairs = Operations.apply(background, action, lastState);

                if (nextStateActionPairs.isPresent()) {

                    // Actions aren't grounded, so each nextState represents a different
                    // paramter binding
                    for (Pair<State, Action> stateActionPair : nextStateActionPairs.get()) {
                        State nextState = stateActionPair.first();
                        Action nextAction = stateActionPair.second();

                        // Prune already visited states
                        if (history.contains(nextState)) {
                            continue;
                        }

                        List<State> next_states = new ArrayList<State>(previous_states);
                        next_states.add(nextState);

                        List<Action> next_actions = new ArrayList<Action>(previous_actions);
                        next_actions.add(nextAction);

                        // Add new state to history and search space
                        search.add(Triple.of(next_states, next_actions, currentDepth + 1));
                        history.add(nextState);
                    }
                }
            }

        }

        return Optional.empty();
    }

    @Override
    public Optional<Set<Plan>> plan(PlanningProblem problem, Set<Formula> background, Set<Action> actions, State start, State goal) {
        return Optional.empty();
    }


     public Optional<Set<Plan>> plan(PlanningProblem problem, Set<Formula> background, Set<Action> actions, State start, State goal, List<PlanMethod> planMethods){
        return Optional.empty();
    }


    public Optional<Plan> verify(Set<Formula> background, State start, State goal, PlanSketch planSketch){
        return Optional.empty();
    }

}