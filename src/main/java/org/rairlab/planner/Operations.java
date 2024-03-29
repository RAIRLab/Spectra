package org.rairlab.planner;

import org.rairlab.planner.utils.Visualizer;
import org.rairlab.shadow.prover.core.Prover;
import org.rairlab.shadow.prover.core.ccprovers.CognitiveCalculusProver;
import org.rairlab.shadow.prover.core.proof.Justification;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.representations.formula.*;
import org.rairlab.shadow.prover.representations.value.Constant;


import org.rairlab.shadow.prover.utils.CollectionUtils;

import org.rairlab.shadow.prover.utils.Sets;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class Operations {

    private static boolean DEEP_EQUIVALENCE = false;
    private static boolean THROW_AWAY_EMPTY_BINDINGS = true;
    private static boolean MONOTONIC = true;
    private static Prover prover;


    private static final Map<Pair<Set<Formula>, Formula>, Optional<Justification>> proverCache = CollectionUtils.newMap();
    private static final Map<Triple<Set<Formula>, Formula, List<Variable>>, Optional<Set<Map<Variable, Value>>>> proverBindingsCache = CollectionUtils.newMap();
    private static final Map<Triple<Set<Formula>, Action, State>, Optional<Set<Pair<State, Action>>> > applyCache = CollectionUtils.newMap();


    public static void reset(){

        proverCache.clear();
        proverBindingsCache.clear();
        applyCache.clear();
    }
    static {
        prover =  new CognitiveCalculusProver();
    }

    public static synchronized Optional<Justification> proveCached(Set<Formula> assumptions, Formula goal) {
        Pair<Set<Formula>, Formula> inputPair = ImmutablePair.of(assumptions, goal);

        if (MONOTONIC) {
            // (1) If we've asked to prove this exact goal from assumptions before
            // then return the previous result
            if (proverCache.containsKey(inputPair)) {
                return proverCache.get(inputPair);
            }

            // Iterate through the cache
            for (Map.Entry<Pair<Set<Formula>, Formula>, Optional<Justification>> entry : proverCache.entrySet()) {
                Set<Formula> cachedAssumptions = entry.getKey().getLeft();
                Formula cachedGoal = entry.getKey().getRight();
                Optional<Justification> optJust = entry.getValue();

                // (2) Return the cached justification if:
                // - Goals are the same
                // - The cached assumptions are a subset of the current ones
                // - A justification was found
                if (optJust.isPresent() && cachedGoal.equals(goal) && Sets.subset(cachedAssumptions, assumptions)) {
                    return optJust;
                }

                // (3) Return cached failure if:
                // - Goals are the same
                // - Assumptions are a subset of cached assumptions
                // - No justification was found
                if (optJust.isEmpty() && cachedGoal.equals(goal) && Sets.subset(assumptions, cachedAssumptions)) {
                    return optJust;
                }
            }
    }

        // Otherwise create a new call to the theorem prover
        Optional<Justification> answer = prover.prove(assumptions, goal);
        if (MONOTONIC) {
            proverCache.put(inputPair, answer);
        }
        return answer;

    }

    public static synchronized Optional<Set<Map<Variable, Value>>> proveAndGetBindingsCached(Set<Formula> assumptions, Formula goal, List<Variable> variables) {

        // (1) If we've asked to find the variables that satisfy this exact goal from assumptions before
        // then return the previous result
        Triple<Set<Formula>, Formula, List<Variable>> inputTriple = Triple.of(assumptions, goal, variables);
        if (proverBindingsCache.containsKey(inputTriple)) {
            return proverBindingsCache.get(inputTriple);
        }

        for (Map.Entry<Triple<Set<Formula>, Formula, List<Variable>>, Optional<Set<Map<Variable, Value>>>> entry : proverBindingsCache.entrySet()) {
            Set<Formula> cachedAssumptions = entry.getKey().getLeft();
            Formula cachedGoal = entry.getKey().getMiddle();
            List<Variable> cachedVars = entry.getKey().getRight();
            Optional<Set<Map<Variable, Value>>> optMapping = entry.getValue();

            // (2) Return the cached justification if:
            // - Goals are the same
            // - The variable list requested is the same
            // - The cached assumptions are a subset of the current ones
            // - A justification was found
            if (optMapping.isPresent() && cachedGoal.equals(goal) && cachedVars.equals(variables) && Sets.subset(cachedAssumptions, assumptions)) {
                return optMapping;
            }

            // (3) Return cached failure if:
            // - Goals are the same
            // - The variable list requested is the same
            // - Assumptions are a subset of cached assumptions
            // - No justification was found
            if (optMapping.isEmpty() && cachedGoal.equals(goal) && cachedVars.equals(variables) && Sets.subset(assumptions, cachedAssumptions)) {
                return optMapping;
            }
        }

        // Otherwise create a new call to the theorem prover
        Optional<Set<Map<Variable, Value>>> answer = proveAndGetMultipleBindings(assumptions, goal, variables);
        proverBindingsCache.put(inputTriple, answer);
        return answer;
    }

    public static synchronized Optional<Map<Variable, Value>> proveAndGetBindings(Set<Formula> givens, Formula goal, List<Variable> variables) {

        Future<Optional<Map<Variable, Value>>> future = new FutureTask<>(() -> {
            return prover.proveAndGetBindings(givens, goal, variables);
        });

        Optional<Map<Variable, Value>> answer;

        try {
            answer = future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            answer = Optional.empty();
        }

        return answer;
    }


    public static synchronized Optional<Set<Map<Variable, Value>>> proveAndGetMultipleBindings(Set<Formula> givens, Formula goal, List<Variable> variables) {

        Optional<org.apache.commons.lang3.tuple.Pair<Justification, Set<Map<Variable, Value>>>> ans  = prover.proveAndGetMultipleBindings(givens, goal, variables);

        if (ans.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(ans.get().getRight());
    }

    public static Optional<Set<Pair<State, Action>>> apply(Set<Formula> background, Action action, State state) {

        // // Get resulting states from cache if computed before
        if(applyCache.containsKey(Triple.of(background, action, state))){
            Optional<Set<Pair<State, Action>>>  ans  = applyCache.get(Triple.of(background, action, state));
            if(ans.isPresent()){
                return applyCache.get(Triple.of(background, action, state));

            }
        }

        // Ask theorem prover for witnesses that satisfy the precondition
        Set<Formula> givens = Sets.union(background, state.getFormulae());

        Formula precondition = action.getPrecondition();

        List<Variable> openVars = action.openVars()
            .stream()
            .collect(Collectors.toList());

        Optional<Set<Map<Variable, Value>>> bindingsOpt = proveAndGetBindingsCached(givens, precondition, openVars);

        // If not witnesses found, return nothing
        if (!bindingsOpt.isPresent()) {
            applyCache.put(Triple.of(background, action, state), Optional.empty());
            return Optional.empty();

        }

        Visualizer.nested(action.getName());
        Set<Pair<State, Action>> nextStates = Sets.newSet();

        for (Map<Variable, Value> binding : bindingsOpt.get()) {

            if (THROW_AWAY_EMPTY_BINDINGS && binding.values().stream().anyMatch(x -> x instanceof Variable)) {
                continue;
            }

            // Apply binding to get grounded action and calculate the next state
            // newState = (oldState - Deletions(a)) U Additions(a)
            Action groundedAction = action.instantiate(binding);

            Set<Formula> additions = groundedAction.getAdditions();
            Set<Formula> deletions = groundedAction.getDeletions();

            State newState = State.initializeWith(Sets.union(
                Sets.difference(state.getFormulae(), deletions),
                additions
            ));

            // If the state progresses, record it as a possible next state
            if (!newState.equals(state)) {
                nextStates.add(ImmutablePair.of(newState, groundedAction));
            }
        }

        applyCache.put(Triple.of(background, action, state), Optional.of(nextStates));

        return Optional.of(nextStates);

    }

    public static boolean equivalent(Set<Formula> background, Formula f1, Formula f2) {
        if (!DEEP_EQUIVALENCE) {
            return f1.equals(f2);
        }

        BiConditional biConditional = new BiConditional(f1, f2);
        return proveCached(background, biConditional).isPresent();
    }

    public static boolean satisfies(Set<Formula> background, State state, State goal) {
        if ((Sets.union(background, state.getFormulae()).containsAll(goal.getFormulae()))) {
            return true;
        }

        for (Formula g : goal.getFormulae()) {
            Optional<Justification> just = proveCached(
                Sets.union(background, state.getFormulae()),
                g
            );
            if (just.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static boolean conflicts(Set<Formula> background, State state1, State state2) {
        return proveCached(
            Sets.union(background, Sets.union(state1.getFormulae(), state2.getFormulae())),
            State.FALSE
        ).isPresent();
    }

}
