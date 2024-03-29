package org.rairlab.planner.search;

import org.rairlab.shadow.prover.representations.formula.Formula;

import org.rairlab.planner.State;
import org.rairlab.planner.Action;
import org.rairlab.planner.Plan;
import org.rairlab.planner.Operations;
import org.rairlab.shadow.prover.representations.value.Value;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

class AStarComparator implements Comparator<Pair<State, List<Action>>> {
    private Map<Pair<State, List<Action>>, Integer> heuristic;

    public AStarComparator() {
        this.heuristic = new HashMap<Pair<State, List<Action>>, Integer>();
    }

    @Override
    public int compare(Pair<State, List<Action>> o1, Pair<State, List<Action>> o2) {
        // Print nag message if undefined behavior is happening
        if (!this.heuristic.containsKey(o1) || !this.heuristic.containsKey(o2)) {
            System.out.println("[ERROR] Heuristic is not defined for state");
        }

        int i1 = this.heuristic.get(o1);
        int i2 = this.heuristic.get(o2);
        return i1 < i2 ? -1: 1;
    }

    public void setValue(Pair<State, List<Action>> k, int v) {
        this.heuristic.put(k, v);
    }

    public int getValue(Pair<State, List<Action>> k) {
        return this.heuristic.get(k);
    }
}

/**
 * Created by brandonrozek on 03/29/2023.
 */
public class AStarPlanner {

    // The longest plan to search for, -1 means no bound
    private Optional<Integer> MAX_DEPTH = Optional.empty();
    // Number of plans to look for, -1 means up to max_depth
    private Optional<Integer> K = Optional.empty();

    public AStarPlanner(){ }

    public Set<Plan> plan(Set<Formula> background, Set<Action> actions, State start, State goal, Function<State, Integer> heuristic) {

        // Search Space Data Structures
        Set<State> history = new HashSet<State>();
        // Each node in the search space consists of
        // (state, sequence of actions from initial)
        AStarComparator comparator = new AStarComparator();
        Queue<Pair<State, List<Action>>> search = new PriorityQueue<Pair<State,List<Action>>>(comparator);

        // Submit Initial State
        Pair<State, List<Action>> searchStart = Pair.of(start, new ArrayList<Action>());
        comparator.setValue(searchStart, 0);
        search.add(searchStart);

        // For debugging...
        // Map<State, List<Action>> seq = new HashMap<State, List<Action>>();
        // seq.put(start, new ArrayList<Action>());

        // Current set of plans
        Set<Plan> plansFound = new HashSet<Plan>();

        // AStar Traversal until
        // - No more actions can be applied
        // - Max depth reached
        // - Found K plans
        while (!search.isEmpty()) {


            Pair<State, List<Action>> currentSearch = search.remove();
            State lastState = currentSearch.getLeft();
            List<Action> previous_actions = currentSearch.getRight();

            // System.out.println("--------------------");
            // System.out.println("Considering state with heuristic: " + comparator.getValue(currentSearch));
            // System.out.println("Current Plan: " + seq.get(lastState).toString());
            // System.out.println("Current State: " + lastState.toString());
            // System.out.println("--------------------");

            // Exit loop if we've passed the depth limit
            int currentDepth = previous_actions.size();
            if (MAX_DEPTH.isPresent() && currentDepth > MAX_DEPTH.get()) {
               break;
            }

            // If we're at the goal return
            if (Operations.satisfies(background, lastState, goal)) {
                plansFound.add(new Plan(previous_actions));
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
                // System.out.println("Considering action: " + action.getName());

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
                    List<Action> next_actions = new ArrayList<Action>(previous_actions);
                    next_actions.add(nextAction);

                    // Add to search space
                    Pair<State, List<Action>> futureSearch = Pair.of(nextState, next_actions);
                    int planCost = next_actions.stream().map(Action::getCost).reduce(0, (a, b) -> a + b);
                    int heuristicValue = heuristic.apply(nextState);
                    comparator.setValue(futureSearch, planCost + heuristicValue);
                    search.add(futureSearch);

                    // For debugging...
                    // seq.put(nextState, next_actions);

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
