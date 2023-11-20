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

        // (1) If we've asked to prove this exact goal from assumptions before
        // then return the previous result
        Pair<Set<Formula>, Formula> inputPair = ImmutablePair.of(assumptions, goal);
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

        // Otherwise create a new call to the theorem prover
        Optional<Justification> answer = prover.prove(assumptions, goal);
        proverCache.put(inputPair, answer);
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

    public static Value getTime(int time) {
        return new Constant("t" + time);
    }

    public static int getTime(Value time) {
        String s = time.getName();
        String[] ss = s.split("t");
        if (ss.length != 2) {
            return -1;
        }
        try {
            int t = Integer.parseInt(ss[1]);
            return t + 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Take a time value, get the integer number out and
    // increment by 1
    public static Value incrementTime(Value time) {
        int t = getTime(time);
        if (t < 0) {
            return new Constant("ERROR");
        }
        return new Constant("t" + (t + 1));
    }

    public static Optional<Set<Pair<State, Action>>> apply(Set<Formula> background, Action action, State state, Value t) {

        // // Get resulting states from cache if computed before
        if(applyCache.containsKey(Triple.of(background, action, state))){
            Optional<Set<Pair<State, Action>>>  ans  = applyCache.get(Triple.of(background, action, state));
            if(ans.isPresent()){
                return applyCache.get(Triple.of(background, action, state));

            }
        }

        // Ask theorem prover for witnesses that satisfy the precondition
        Set<Formula> givens = Sets.union(background, state.getFormulae());

        // TODO: Have all this ?now and (next ?now) code within Action.java

        // Replace ?now with current time within preconditions
        Formula precondition = action.getPrecondition();
        Value now = new Variable("?now");
        precondition = replaceValue(precondition, now, t);

        // We already replaced the ?now
        List<Variable> openVars = action.openVars()
            .stream()
            .filter(v -> !v.getName().equals("?now"))
            .collect(Collectors.toList());

        // TODO: Can likely more intelligently cache considering time...
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

            // Replace (next ?now) with appropriate time
            Value nextTime = incrementTime(t);
            Value nextTimeVar = new Variable("?next");
            additions = replaceValue(additions, nextTimeVar, nextTime);
            deletions = replaceValue(deletions, nextTimeVar, nextTime);


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

    public static State replaceValue(State s, Value r, Value t) {
        Set<Formula> newFormulae = replaceValue(s.getFormulae(), r, t);
        return State.initializeWith(newFormulae);
    }

    public static Set<Formula> replaceValue(Set<Formula> s, Value r, Value t) {
        Set<Formula> newFormulae = new HashSet<Formula>();
        for (Formula f : s) {
            newFormulae.add(replaceValue(f, r, t));
        }
        return newFormulae;
    }

    public static Value replaceValue(Value v, Value r, Value t) {
        if (v.getName().equals(r.getName())) {
            return t;
        }
        return v;
    }

    // Everywhere where there's a ?now replace with value t
    public static Formula replaceValue(Formula f, Value r, Value t) {
        // Base Cases:

        // Bottom of Formula graph wouldn't have any time points under it
        if (f instanceof Predicate || f instanceof Atom) {
            return f;
        }

        // Check if these quantifiers contain our bound varialbe
        if (f instanceof UnaryModalFormula) {
            UnaryModalFormula uf = (UnaryModalFormula) f;

            Value agent = uf.getAgent();
            Value time = uf.getTime();
            Value newTime = replaceValue(time, r, t);
            Formula uf_sub = uf.getFormula();
            Formula new_uf_sub = replaceValue(uf_sub, r, t);

            if (f instanceof Belief) {
                return new Belief(agent, newTime, new_uf_sub);
            } else if (f instanceof Intends) {
                return new Intends(agent, newTime, new_uf_sub);
            } else if (f instanceof Knowledge) {
                return new Knowledge(agent, newTime, new_uf_sub);
            }
            // Assumes Perception
            if (! (f instanceof Perception)) {
                System.out.println("[fixTimepoints:Operations.java] Doesn't account for new modal operator");
            }
            return new Perception(agent, newTime, new_uf_sub);
        }

        // Recusive Case: Iterate over each subformula and replace

        if (f instanceof Not) {
            Formula subFormula = ((Not) f).getArgument();
            return new Not(replaceValue(subFormula, r, t));
        } else if (f instanceof Universal) {
            Formula subFormula = ((Universal) f).getArgument();
            return new Universal(((Universal) f).vars(), replaceValue(subFormula, r, t));
        } else if (f instanceof Existential) {
            Formula subFormula = ((Existential) f).getArgument();
            return new Universal(((Existential) f).vars(), replaceValue(subFormula, r, t));
        } else if (f instanceof Implication) {
            Formula antecedant = ((Implication) f).getAntecedent();
            Formula consequent = ((Implication) f).getConsequent();
            return new Implication(replaceValue(antecedant, r, t), replaceValue(consequent, r, t));
        } else if (f instanceof BiConditional) {
            Formula left = ((BiConditional) f).getLeft();
            Formula right = ((BiConditional) f).getRight();
            return new BiConditional(replaceValue(left, r, t), replaceValue(right, r, t));
        }

        List<Formula> subFormulae = f.getArgs();
        List<Formula> newArguments = new ArrayList<Formula>();
        for (Formula sf : subFormulae) {
            newArguments.add(replaceValue(sf, r, t));
        }

        if (f instanceof And) {
            return new And(newArguments);
        }

        // Assume Or
        if (! (f instanceof Or)) {
            System.out.println("[fixTimepoints:Operations.java] Not accounting for formula type in recursive case");
        }
        return new Or(newArguments);
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
