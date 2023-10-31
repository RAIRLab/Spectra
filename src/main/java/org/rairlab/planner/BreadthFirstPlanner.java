package org.rairlab.planner;

import org.rairlab.shadow.prover.representations.formula.Formula;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by brandonrozek on 03/29/2023.
 */
public class BreadthFirstPlanner {

    // The longest plan to search for, -1 means no bound
    private Optional<Integer> MAX_DEPTH = Optional.empty();
    // Number of plans to look for, -1 means up to max_depth
    private Optional<Integer> K = Optional.empty();

    public BreadthFirstPlanner(){ }

    public Set<Plan> plan(Set<Formula> background, Set<Action> actions, State start, State goal) {

        // Search Space Data Structures
        Set<State> history = new HashSet<State>();
        // Each node in the search space consists of
        // (state, sequence of actions from initial)
        Queue<Pair<List<State>, List<Action>>> search = new ArrayDeque<Pair<List<State>,List<Action>>>();

        // Submit Initial State
        search.add(Pair.of(List.of(start), new ArrayList<Action>()));

        // Current set of plans
        Set<Plan> plansFound = new HashSet<Plan>();

        // Breadth First Traversal until
        // - No more actions can be applied
        // - Max depth reached
        // - Found K plans
        while (!search.isEmpty()) {

            Pair<List<State>, List<Action>> currentSearch = search.remove();
            List<State> previous_states = currentSearch.getLeft();
            List<Action> previous_actions = currentSearch.getRight();
            State lastState = previous_states.get(previous_states.size() - 1);

            // Exit loop if we've passed the depth limit
            int currentDepth = previous_actions.size();
            if (MAX_DEPTH.isPresent() && currentDepth > MAX_DEPTH.get()) {
               break;
            }

            // If we're at the goal return
            if (Operations.satisfies(background, lastState, goal)) {
                plansFound.add(new Plan(previous_actions, previous_states, background));
                if (K.isPresent() && plansFound.size() >= K.get()) {
                    break;
                }
                continue;
            }

            // Only consider non-trivial actions
            Set<Action> nonTrivialActions = actions.stream()
                .filter(Action::isNonTrivial)
                .collect(Collectors.toSet());

            // Apply the action to the state and add to the search space
            for (Action action : nonTrivialActions) {
                Optional<Set<Pair<State, Action>>> optNextStateActionPairs = Operations.apply(background, action, lastState);

                // Ignore actions that aren't applicable
                if (optNextStateActionPairs.isEmpty()) {
                    continue;
                }

                // Action's aren't grounded so each nextState represents
                // a different parameter binding
                Set<Pair<State, Action>> nextStateActionPairs = optNextStateActionPairs.get();
                for (Pair<State, Action> stateActionPair: nextStateActionPairs) {
                    State nextState = stateActionPair.getLeft();
                    Action nextAction = stateActionPair.getRight();

                    // Prune already visited states
                    if (history.contains(nextState)) {
                        continue;
                    }

                    // Add to history
                    history.add(nextState);

                    // Construct search space parameters
                    List<State> next_states = new ArrayList<State>(previous_states);
                    next_states.add(nextState);

                    List<Action> next_actions = new ArrayList<Action>(previous_actions);
                    next_actions.add(nextAction);

                    // Add to search space
                    search.add(Pair.of(next_states, next_actions));
                }
            }
        }

        return plansFound;
    }

    public Optional<Integer> getMaxDepth() {
        return MAX_DEPTH;
    }

    public void setMaxDepth(int maxDepth) {
        MAX_DEPTH = Optional.of(maxDepth);
    }

    public void setK(int k) {
        K = Optional.of(k);
    }

    public void clearK() {
        K = Optional.empty();
    }

    public Optional<Integer> getK() {
        return K;
    }

}