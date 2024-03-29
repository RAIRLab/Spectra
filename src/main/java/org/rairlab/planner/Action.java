package org.rairlab.planner;

import org.rairlab.shadow.prover.core.Logic;
import org.rairlab.shadow.prover.representations.formula.And;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Compound;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
 import org.rairlab.shadow.prover.utils.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Created by naveensundarg on 1/13/17.
 */
public class Action {

    private final Set<Formula> preconditions;
    private final Set<Formula> additions;
    private final Set<Formula> deletions;
    private final List<Variable> freeVariables;
    private final List<Variable> interestedVars;

    private final String name;
    private final Formula precondition;

    private int cost;
    private int weight;
    private final boolean  trivial;

    private final Compound shorthand;

    public Action(String name, Set<Formula> preconditions, Set<Formula> additions, Set<Formula> deletions, int cost, List<Variable> freeVariables, List<Variable> interestedVars) {
        this.name = name;
        this.preconditions = preconditions;

        this.additions = additions;
        this.deletions = deletions;
        List<Variable> computedFreeVariables = preconditions.
                stream().
                map(x -> Sets.difference(x.variablesPresent(), x.boundVariablesPresent())).
                reduce(Sets.newSet(), Sets::union).
                stream().sorted().collect(Collectors.toList());

        this.freeVariables = freeVariables;

        if (preconditions.size() > 1) {
            this.precondition = new And(preconditions.stream().collect(Collectors.toList()));
        } else if (preconditions.size() == 1) {
            this.precondition = preconditions.iterator().next();
        } else {
            this.precondition = State.TRUE;
        }


        this.weight = preconditions.stream().mapToInt(Formula::getWeight).sum() +
                additions.stream().mapToInt(Formula::getWeight).sum() +
                deletions.stream().mapToInt(Formula::getWeight).sum();
        this.cost = cost;

        List<Value> valuesList = interestedVars.stream().collect(Collectors.toList());;
        this.shorthand = new Compound(name, valuesList);

        this.trivial = computeTrivialOrNot();
        this.interestedVars = interestedVars;
    }

    public Action(String name, Set<Formula> preconditions, Set<Formula> additions,
                   Set<Formula> deletions, int cost, List<Variable> freeVariables,
                   Compound shorthand
    ) {
        this.name = name;
        this.preconditions = preconditions;

        this.additions = additions;
        this.deletions = deletions;
        List<Variable> computedFreeVariables = preconditions.
                stream().
                map(x -> Sets.difference(x.variablesPresent(), x.boundVariablesPresent())).
                reduce(Sets.newSet(), Sets::union).
                stream().sorted().collect(Collectors.toList());

        this.freeVariables = freeVariables;

        this.precondition = new And(preconditions.stream().collect(Collectors.toList()));

        this.weight = preconditions.stream().mapToInt(Formula::getWeight).sum() +
                additions.stream().mapToInt(Formula::getWeight).sum() +
                deletions.stream().mapToInt(Formula::getWeight).sum();
        this.cost = cost;

        this.shorthand = shorthand;
        this.trivial = computeTrivialOrNot();
        this.interestedVars = freeVariables;

    }


    public static Action buildActionFrom(String name,
                                         Set<Formula> preconditions,
                                         Set<Formula> additions,
                                         Set<Formula> deletions,
                                         int cost,
                                         List<Variable> freeVariables) {

        return new Action(name, preconditions, additions, deletions, cost, freeVariables, freeVariables);

    }

    public static Action buildActionFrom(String name,
                                         Set<Formula> preconditions,
                                         Set<Formula> additions,
                                         Set<Formula> deletions,
                                         int cost,
                                         List<Variable> freeVariables, List<Variable> interestedVars) {

        return new Action(name, preconditions, additions, deletions, cost, freeVariables, interestedVars);

    }

    public int getWeight() {
        return weight;
    }

    public int getCost() {
        return cost;
    }

    public Formula getPrecondition() {
        return precondition;
    }

    public Set<Formula> getPreconditions() {
        return preconditions;
    }

    public Set<Formula> getAdditions() {
        return additions;
    }

    public Set<Formula> getDeletions() {
        return deletions;
    }

    public List<Variable> openVars() {
        return freeVariables;
    }

    public List<Variable> getInterestedVars() {
        return interestedVars;
    }

    public Set<Formula> instantiateAdditions(Map<Variable, Value> mapping) {

        return additions.stream().map(x -> x.apply(mapping)).collect(Collectors.toSet());
    }

    public Set<Formula> instantiateDeletions(Map<Variable, Value> mapping) {

        return deletions.stream().map(x -> x.apply(mapping)).collect(Collectors.toSet());
    }


    public Action instantiate(Map<Variable, Value> binding){

        // Apply binding to precondition, additions, and deletions
        Set<Formula> newPreconditions = preconditions.stream().map(x->x.apply(binding)).collect(Collectors.toSet());
        Set<Formula> newAdditions = additions.stream().map(x->x.apply(binding)).collect(Collectors.toSet());
        Set<Formula> newDeletions = deletions.stream().map(x->x.apply(binding)).collect(Collectors.toSet());

        // Allow for partial instantiation, grab variables that aren't
        List<Variable> newFreeVariables = CollectionUtils.newEmptyList();
        for(Variable var: freeVariables){

            if(!binding.keySet().contains(var)){
                newFreeVariables.add(var);
            }
        }

        List<Value> valuesList = interestedVars.stream().collect(Collectors.toList());;
        Compound shorthand = (Compound)(new Compound(name, valuesList)).apply(binding);
        return new Action(name, newPreconditions, newAdditions, newDeletions, cost, newFreeVariables, shorthand);
    }

    public String getName() {
        return name;
    }

    public boolean isNonTrivial() {
        return !trivial;
    }

    public boolean computeTrivialOrNot(){

        // All the additions are already in the preconditions and nothing gets deleted
        boolean case1Trivial =  Sets.subset(additions, preconditions) && deletions.isEmpty();

        // There are no additions and the deletes that are there are already negated in the precondition so they don't exist.
        boolean case2Trivial =  additions.isEmpty() && deletions.stream().allMatch(x->preconditions.stream().anyMatch(y->y.equals(Logic.negated(x))));

        // All additions are already in preconditions and the deletions are negated in the preconditions
        boolean case3Trivial = Sets.subset(additions, preconditions) && deletions.stream().allMatch(x->preconditions.stream().anyMatch(y->y.equals(Logic.negated(x))));

        return case1Trivial || case2Trivial || case3Trivial;
    }

    public Compound getShorthand() {
        return shorthand;
    }

    @Override
    public String toString() {
        return shorthand.getArguments().length == 0?  name: shorthand.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (!preconditions.equals(action.preconditions)) return false;
        if (!additions.equals(action.additions)) return false;
        if (!deletions.equals(action.deletions)) return false;
        return name.equals(action.name);
    }

    @Override
    public int hashCode() {
        int result = preconditions.hashCode();
        result = 31 * result + additions.hashCode();
        result = 31 * result + deletions.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

}
