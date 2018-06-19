package com.naveensundarg.planner;

import com.naveensundarg.planner.utils.Visualizer;
import com.naveensundarg.shadow.prover.core.Prover;
import com.naveensundarg.shadow.prover.core.SnarkWrapper;
import com.naveensundarg.shadow.prover.core.proof.Justification;
import com.naveensundarg.shadow.prover.representations.formula.BiConditional;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.formula.Predicate;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.ImmutablePair;
import com.naveensundarg.shadow.prover.utils.Pair;

import com.naveensundarg.shadow.prover.utils.Sets;
import org.apache.commons.lang3.tuple.Triple;

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
        prover = SnarkWrapper.getInstance();


    }

    public static synchronized Optional<Justification> proveCached(Set<Formula> assumptions, Formula goal) {

        Pair<Set<Formula>, Formula> inputPair = ImmutablePair.from(assumptions, goal);

        if (proverCache.containsKey(inputPair)) {

            return proverCache.get(inputPair);

        }

        Optional<Map.Entry<Pair<Set<Formula>, Formula>, Optional<Justification>>> cachedOptionalSuccessful = proverCache.entrySet().stream().filter(pairOptionalEntry -> {

            Set<Formula> cachedAssumptions = pairOptionalEntry.getKey().first();
            Formula cachedGoal = pairOptionalEntry.getKey().second();

            return cachedGoal.equals(goal) && Sets.subset(cachedAssumptions, assumptions);
        }).findAny();


        if(cachedOptionalSuccessful.isPresent() && cachedOptionalSuccessful.get().getValue().isPresent()){

            return cachedOptionalSuccessful.get().getValue();
        }


        Optional<Map.Entry<Pair<Set<Formula>, Formula>, Optional<Justification>>> cachedOptionalFailed = proverCache.entrySet().stream().filter(pairOptionalEntry -> {

            Set<Formula> cachedAssumptions = pairOptionalEntry.getKey().first();
            Formula cachedGoal = pairOptionalEntry.getKey().second();

            return cachedGoal.equals(goal) && Sets.subset(assumptions, cachedAssumptions);
        }).findAny();


        if(cachedOptionalFailed.isPresent() && !cachedOptionalFailed.get().getValue().isPresent()){

            return cachedOptionalFailed.get().getValue();
        }

        if(goal instanceof Predicate && ((Predicate) goal).getName().equals("sameroom")){

            Predicate p = (Predicate) goal;

            Value v1 = p.getArguments()[0];
            Value v2 = p.getArguments()[1];

            Optional<Formula> inOptv1 = assumptions.stream().filter(x-> x instanceof Predicate &&
                    ((Predicate)x).getName().equals("in") && ((Predicate) x).getArguments()[0].equals(v1)).findAny();

             Optional<Formula> inOptv2 = assumptions.stream().filter(x-> x instanceof Predicate &&
                    ((Predicate)x).getName().equals("in") && ((Predicate) x).getArguments()[0].equals(v2)).findAny();


             if(inOptv1.isPresent() && inOptv2.isPresent()){

                 Value room1 = ((Predicate)inOptv1.get()).getArguments()[1];
                 Value room2 = ((Predicate)inOptv2.get()).getArguments()[1];

                 if(room1.equals(room2)){

                     return Optional.of(Justification.trivial(assumptions, goal));
                 }

             }


        }

        {

            Optional<Justification> answer = prover.prove(assumptions, goal);

            proverCache.put(inputPair, answer);

            return answer;
        }

    }

    public static synchronized Optional<Set<Map<Variable, Value>>> proveAndGetBindingsCached(Set<Formula> givens, Formula goal, List<Variable> variables) {


        Triple<Set<Formula>, Formula, List<Variable>> inputTriple = Triple.of(givens, goal, variables);

        if (proverBindingsCache.containsKey(inputTriple)) {

            return proverBindingsCache.get(inputTriple);

        } else {

            Optional<Set<Map<Variable, Value>>> answer = proveAndGetMultipleBindings(givens, goal, variables);

            proverBindingsCache.put(inputTriple, answer);

            return answer;
        }

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

        if(ans.isPresent()){

           return Optional.of(ans.get().getRight());

        }else {
            return Optional.empty();
        }
      /*  Future<Optional<Set<Map<Variable, Value>>>> future = new FutureTask<>(()-> prover.proveAndGetMultipleBindings(givens, goal, variables));

        Optional<Set<Map<Variable, Value>>> answer;

        try{

            answer = future.get(50, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e ) {
            answer =   Optional.empty();
        }
                return answer;

*/
    }


    public static Optional<Set<Pair<State, Action>>> apply(Set<Formula> background, Action action, State state) {


        if(applyCache.containsKey(Triple.of(background, action, state))){

            Optional<Set<Pair<State, Action>>>  ans  = applyCache.get(Triple.of(background, action, state));
            if(ans.isPresent()){
                return applyCache.get(Triple.of(background, action, state));

            }
        }

        Set<Formula> givens = Sets.union(background, state.getFormulae());

        Optional<Set<Map<Variable, Value>>> bindingsOpt = proveAndGetBindingsCached(givens, action.getPrecondition(), action.openVars());

        State newState;

        if (!bindingsOpt.isPresent()) {



            applyCache.put(Triple.of(background, action ,state), Optional.empty());
            return Optional.empty();

        }

        Visualizer.nested(action.getName());

        Set<Pair<State, Action>> nexts = Sets.newSet();
        for (Map<Variable, Value> binding : bindingsOpt.get()) {

            if (THROW_AWAY_EMPTY_BINDINGS && binding.values().stream().anyMatch(x -> x instanceof Variable)) {

                continue;
            }

            Set<Formula> instantiatedDeletions = action.instantiateDeletions(binding);

            Set<Formula> formulaeToRemove = state.getFormulae().stream().
                    filter(f -> instantiatedDeletions.stream().anyMatch(d -> equivalent(Sets.union(background, state.getFormulae()), f, d))).collect(Collectors.toSet());

            Set<Formula> newFormulae = Sets.union(background, state.getFormulae());

            newFormulae = Sets.union(newFormulae, action.instantiateAdditions(binding).stream().map(Simplifier::simplify).collect(Collectors.toSet()));


            newFormulae = Sets.difference(newFormulae, formulaeToRemove);


            newState = State.initializeWith(newFormulae);

            nexts.add(ImmutablePair.from(newState, action.instantiate(binding)));


        }


        if (nexts.isEmpty()) {

            Map<Variable, Value> emptyBinding = CollectionUtils.newMap();
            Set<Formula> instantiatedDeletions = action.instantiateDeletions(emptyBinding);

            Set<Formula> formulaeToRemove = state.getFormulae().stream().
                    filter(f -> instantiatedDeletions.stream().anyMatch(d -> equivalent(background, f, d))).collect(Collectors.toSet());

            Set<Formula> newFormulae = state.getFormulae();

            newFormulae = Sets.union(newFormulae, action.instantiateAdditions(emptyBinding));


            newFormulae = Sets.difference(newFormulae, formulaeToRemove);


            newState = State.initializeWith(newFormulae);

            nexts.add(ImmutablePair.from(newState, action.instantiate(emptyBinding)));


        }

        nexts = nexts.stream().filter(n-> !n.first().getFormulae().equals(state.getFormulae())).collect(Collectors.toSet());;



        applyCache.put(Triple.of(background, action ,state), Optional.of(nexts));

        return Optional.of(nexts);

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
        return goal.getFormulae().stream().
                allMatch(x -> proveCached(Sets.union(background, state.getFormulae()), x).isPresent());

    }

    public static boolean conflicts(Set<Formula> background, State state1, State state2) {


        return proveCached(Sets.union(background, Sets.union(state1.getFormulae(), state2.getFormulae())), State.FALSE).isPresent();

    }

}
